package com.contatodireto.eventflow.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterDto(@NotBlank String name,
                              @NotBlank String email,
                              @NotBlank String password) {
}
