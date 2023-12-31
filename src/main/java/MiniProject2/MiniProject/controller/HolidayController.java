package MiniProject2.MiniProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import MiniProject2.MiniProject.Utils;
import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.service.HolidayService;

@Controller
@RequestMapping(path = {"/start","/"})
public class HolidayController {

    @Autowired
    @Qualifier(Utils.BEAN_REDIS)
    private RedisTemplate<String, String> template;
    
    @Autowired
    private HolidayService holidayService;


    @GetMapping(path = {"/start","/"})
    public ModelAndView showForm() {
        ModelAndView mav = new ModelAndView("start");
        HolidayDates holidayDates = new HolidayDates();
        mav.addObject("holidayDates", holidayDates);
        return mav;
    }

    @PostMapping("/submit")
    public ModelAndView submitForm(@ModelAttribute("holidayDates") HolidayDates holidayDates, BindingResult result) {
        try {
            System.out.println("Submitted data: " + holidayDates);
            holidayService.addHoliday(holidayDates);
            return new ModelAndView("redirect:/checker");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("error");
        }
    }
}
