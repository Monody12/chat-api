package com.example.chatapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/","/index"})
    public String index(){
        return "index.html";
    }

    @GetMapping(value = {"/login"})
    public String login(){
        return "login.html";
    }

    @GetMapping(value = {"/register"})
    public String register(){
        return "register.html";
    }

}
