package MiniProject2.MiniProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.repo.RedisHolidayRepository;
import MiniProject2.MiniProject.service.HolidayService;
import java.util.List;

@Controller
public class HolidayCheckerController {


    @Autowired
    private RedisHolidayRepository holidayRepository;

    @Autowired
    private HolidayService holidaySvc;

    @GetMapping("/checker")
    public ModelAndView showCheckerPage() {
        List<HolidayDates> holidayDatesList = holidayRepository.findAll();
        List<HolidayDates> overlappingDates = holidaySvc.findOverlappingDatesForAllUsers();

        ModelAndView mav = new ModelAndView();
        mav.addObject("holidayDatesList", holidayDatesList);
        mav.addObject("overlappingDates", overlappingDates);
        mav.setViewName("checker");
        return mav;
    }

    @GetMapping("/return-to-main")
    public String returnToMainPage() {
        return "redirect:/start";
    }

}

