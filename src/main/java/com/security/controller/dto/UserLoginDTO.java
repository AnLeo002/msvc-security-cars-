package com.security.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO (@NotBlank String username,
                            @NotBlank String password){
}
