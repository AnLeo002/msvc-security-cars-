package com.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("hello/a")
    public String helloAdmin(){
        return "Hola admin - keycloak";
    }
    @GetMapping("hello/u")
    public String helloUser(){
        return "Hola user - keycloak";
    }
}
