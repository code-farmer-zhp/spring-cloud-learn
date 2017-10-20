package com.feiniu.member.controller.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WelcomeController {
    @Value("${m.my.server}")
    private String mMyServer;

    @RequestMapping(value="/",method = RequestMethod.GET)
    protected ModelAndView welcome(HttpServletRequest request){
        String serverName = request.getServerName();
        if (serverName.equals(mMyServer)) {
            return new ModelAndView("redirect:/touch/comment/index");
        }else {
            return new ModelAndView("redirect:/comment/myCommentView");
        }
    }
}
