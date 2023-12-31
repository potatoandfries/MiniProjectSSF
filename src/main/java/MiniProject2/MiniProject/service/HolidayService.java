package MiniProject2.MiniProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import MiniProject2.MiniProject.Utils;
import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.repo.RedisHolidayRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    @Autowired
    private RedisHolidayRepository repo;

    @Autowired
    @Qualifier(Utils.BEAN_REDIS)
    private RedisTemplate<String, String> template;

    public void addHoliday(HolidayDates newHolidayDates) {
        repo.saveHoliday(newHolidayDates);
    }
    
   
    public List<HolidayDates> findOverlappingDatesForAllUsers() {
        List<HolidayDates> allHolidayDates = repo.findAll();
    
        // Extract start and end dates
        List<LocalDate> startDates = allHolidayDates.stream()
                .map(HolidayDates::getStartDate)
                .collect(Collectors.toList());
    
        List<LocalDate> endDates = allHolidayDates.stream()
                .map(HolidayDates::getEndDate)
                .collect(Collectors.toList());
    
        // Find overlapping dates
        List<HolidayDates> overlappingDates = findFinalOverlappingDates(startDates, endDates);
    
        return overlappingDates;
    }
    
    private List<HolidayDates> findFinalOverlappingDates(List<LocalDate> startDates, List<LocalDate> endDates) {
        List<HolidayDates> overlappingDates = new ArrayList<>();
    
        for (int i = 0; i < startDates.size(); i++) {
            LocalDate startDate1 = startDates.get(i);
            LocalDate endDate1 = endDates.get(i);
    
            // Check if this is the final overlapping date
            boolean isFinalOverlap = true;
    
            for (int j = 0; j < startDates.size(); j++) {
                if (i == j) continue; // Skip comparing with itself
    
                LocalDate startDate2 = startDates.get(j);
                LocalDate endDate2 = endDates.get(j);
    
                // Check for an overlap
                if (!endDate1.isBefore(startDate2) && !endDate2.isBefore(startDate1)) {
                    // Overlapping dates found
                    LocalDate overlapStart = startDate2.isAfter(startDate1) ?
                            startDate2 : startDate1;
    
                    LocalDate overlapEnd = endDate1.isBefore(endDate2) ?
                            endDate1 : endDate2;
    
                    overlappingDates.add(new HolidayDates(null, overlapStart, overlapEnd));
    
                    // This is not the final overlapping date
                    isFinalOverlap = false;
                    break;
                }
            }
    
            // If this is the final overlapping date, break the loop
            if (isFinalOverlap) {
                break;
            }
        }
    
        return overlappingDates;
    }
    public List<HolidayDates> getAllHolidays() {
        return repo.findAll();
    }

    public List<HolidayDates> getUserHolidays(String userId) {
        return repo.findByUserId(userId);
    }

}
    