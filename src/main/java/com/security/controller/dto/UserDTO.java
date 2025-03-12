package com.security.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;
@Builder
public record UserDTO (@NotBlank String username,
                       @NotBlank String email,
                       @NotBlank String firstName,
                       @NotBlank String lastName,
                       @NotBlank String password,
                       @Valid Set<String> roles
                       ){

}
