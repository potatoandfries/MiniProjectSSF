package MiniProject2.MiniProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.repo.RedisHolidayRepository;
import MiniProject2.MiniProject.service.GoogleCalendarService;
import MiniProject2.MiniProject.service.HolidayService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
public class HolidayCheckerController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

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

    @GetMapping("/add-holiday")
    public RedirectView addHolidayPage() {
        try {
            String authorizationUrl = googleCalendarService.getAuthorizationUrl();
            return new RedirectView(authorizationUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("/error");
        }
    }

    @GetMapping("/google-calendar-callback")
    public String googleCalendarCallback(@RequestParam("code") String code) throws IOException, GeneralSecurityException {
       
            googleCalendarService.handleAuthorizationCallback(code);
            return "redirect:/checker";
        }
}
