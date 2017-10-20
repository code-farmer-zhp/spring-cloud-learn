package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/test")
    public Map test() {
        return Collections.singletonMap("message", "Hello World");
    }

    @PostMapping("/test2")
    public Map test2(String name, String age) {
        return Collections.singletonMap("message", name + ";" + age);
    }


    @PostMapping("/test3")
    public Map test3(@RequestBody User user) {
        return Collections.singletonMap("message", user);
    }

}
