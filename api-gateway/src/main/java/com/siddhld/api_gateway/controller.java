package com.siddhld.api_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/test")
    public String get(){
        return "Testing";
    }
}
