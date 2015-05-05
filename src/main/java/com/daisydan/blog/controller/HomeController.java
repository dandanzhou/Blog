package com.daisydan.blog.controller;

import com.daisydan.blog.dao.UserDao;
import com.daisydan.blog.entity.User;
import com.daisydan.blog.service.UserService;
import com.daisydan.blog.utils.ContextUtils;
import com.daisydan.blog.utils.WebUtils;
import com.daisydan.blog.validator.InvalidException;
import com.daisydan.blog.rest.RestData;
import com.daisydan.blog.security.LoginRequired;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * Created by veryyoung on 2015/3/2.
 */
@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;


    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("users", userService.findAll());
        return modelAndView;
    }

    @RequestMapping(value = "/home/about", method = RequestMethod.GET)
    public String getAbout() {
        return "/home/about";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister() {
        return "/register";
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(User user, String captcha) {
        ModelAndView modelAndView = new ModelAndView("/register");
        if (!WebUtils.checkCaptcha(request, captcha)) {
            modelAndView.addObject("error", "验证码错误");
            return modelAndView;
        }

        logger.debug("user:{}", user);
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        user.setCreateTime(new Date());
        try {
            getValidatorWrapper().tryValidate(user);
            userService.create(user);
        } catch (InvalidException ex) {
            logger.error("Invalid User Object: {}", user.toString(), ex);
            modelAndView.addObject("error", ex.getMessage());
            return modelAndView;
        }


        return new ModelAndView("redirect:/index");
    }

    @RequestMapping(value = "/checkUserName", method = RequestMethod.GET)
    @ResponseBody
    public RestData checkUserName(String userName) {
        RestData restData = new RestData();
        if (userDao.checkUserName(userName)) {
            restData.setSuccess(1);
        } else {
            restData.setComment("该用户名已存在");
        }
        return restData;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLogin(String redirect) {
        ModelAndView modelAndView = new ModelAndView("/login");
        if (StringUtils.isNotEmpty(redirect)) {
            modelAndView.addObject("error", "您需要先登录!");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(String userName, String password, String captcha) {
        ModelAndView modelAndView = new ModelAndView("/login");
        if (!WebUtils.checkCaptcha(request, captcha)) {
            modelAndView.addObject("error", "验证码错误");
            return modelAndView;
        }

        User user = userDao.findByUserName(userName);

        if (null != user && user.getPassword().equals(DigestUtils.md5Hex(password))) {
            ContextUtils.getSessionUtils(request).setUser(user);
            return new ModelAndView("redirect:/account");
        } else {
            modelAndView.addObject("error", "用户名或密码错误");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    @LoginRequired
    public String getAccount() {
        return "/account";
    }


}
