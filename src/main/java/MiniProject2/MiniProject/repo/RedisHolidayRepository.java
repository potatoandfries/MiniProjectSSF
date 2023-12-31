package MiniProject2.MiniProject.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import MiniProject2.MiniProject.Utils;
import MiniProject2.MiniProject.model.HolidayDates;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RedisHolidayRepository {

    @Autowired
    @Qualifier(Utils.BEAN_REDIS)
    private RedisTemplate<String, String> template;

    // Updated saveHoliday method to use consistent date format
    public void saveHoliday(HolidayDates holidayDates) {
        String key = "user:" + holidayDates.getUserId();
        String value = String.format("%s-%s",
                holidayDates.getStartDate().format(DateTimeFormatter.ISO_DATE),
                holidayDates.getEndDate().format(DateTimeFormatter.ISO_DATE));

        ListOperations<String, String> opsList = template.opsForList();
        opsList.rightPush(key, value);
    }

    // Inside RedisHolidayRepository class
    public List<HolidayDates> findByUserId(String userId) {
        String key = "user:" + userId;
        ListOperations<String, String> opsList = template.opsForList();
        List<String> dateStrings = opsList.range(key, 0, -1);
    
        return dateStrings.stream()
                .map(dateString -> {
                    try {
                        String startDateString = dateString.substring(0, 10);
                        String endDateString = dateString.substring(10);
    
                        // Ensure that the negative sign is not included in the output
                        endDateString = endDateString.startsWith("-") ? endDateString.substring(1) : endDateString;
    
                        return new HolidayDates(
                                userId,
                                LocalDate.parse(startDateString, DateTimeFormatter.ISO_DATE),
                                LocalDate.parse(endDateString, DateTimeFormatter.ISO_DATE)
                        );
                    } catch (Exception e) {
                        // Handle parsing exceptions, log them, and consider skipping the problematic entry
                        System.err.println("Error parsing date: " + dateString);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    

    public List<HolidayDates> findAll() {
        Set<String> allKeysSet = template.keys("user:*");
        List<HolidayDates> allHolidayDates = new ArrayList<>();
    
        for (String userKey : allKeysSet) {
            String userId = userKey.replace("user:", "").trim();
            List<HolidayDates> userHolidayDates = findByUserId(userId);
            allHolidayDates.addAll(userHolidayDates);
        }
    
        return allHolidayDates;
    }

    public List<String> findAllUserIds() {
        Set<String> allKeysSet = template.keys("user:*");
        return allKeysSet.stream()
                .map(key -> key.replace("user:", "").trim())
                .collect(Collectors.toList());
    }
}
