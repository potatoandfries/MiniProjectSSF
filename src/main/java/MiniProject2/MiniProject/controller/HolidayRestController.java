package MiniProject2.MiniProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.service.HolidayService;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayRestController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping(produces = "application/json")
    public List<HolidayDates> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @PostMapping(consumes = "application/json")
    public void addHoliday(@RequestBody HolidayDates holidayDates) {
        holidayService.addHoliday(holidayDates);
    }

    @GetMapping(value = "/get/{userId}", produces = "application/json")
    public List<HolidayDates> getUserHolidays(@PathVariable String userId) {
        return holidayService.getUserHolidays(userId);
    }

    @GetMapping(value = "/get-overlapping", produces = "application/json")
    public List<HolidayDates> getOverlappingHolidays() {
        return holidayService.findOverlappingDatesForAllUsers();
    }
}

