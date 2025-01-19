package cn.springcamp.spring.advanced.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefualtController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/public")
    public String publicpage() {
        return "index";
    }

    @GetMapping("/private")
    public String privatepage() {
        return "index";
    }

    @GetMapping("/valid")
    public String validpage() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
