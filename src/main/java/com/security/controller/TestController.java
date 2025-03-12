package com.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("hello/a")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmin(){
        return "Hola admin - keycloak";
    }
    @GetMapping("hello/u")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public String helloUser(){
        return "Hola user - keycloak";
    }
}
