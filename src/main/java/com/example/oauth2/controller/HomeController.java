package com.example.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "OAuth2 Security Example");
        return "index";
    }

    @GetMapping("/authorized")
    public String authorized(@RequestParam(required = false) String code,
                           @RequestParam(required = false) String state,
                           @RequestParam(required = false) String error,
                           Model model) {
        
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("message", "Erro na autorização: " + error);
        } else if (code != null) {
            model.addAttribute("code", code);
            model.addAttribute("state", state);
            model.addAttribute("message", "Autorização realizada com sucesso!");
        } else {
            model.addAttribute("message", "Página de callback de autorização");
        }
        
        return "authorized";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}