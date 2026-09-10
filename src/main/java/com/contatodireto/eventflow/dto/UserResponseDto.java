package com.contatodireto.eventflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserResponseDto(@NotNull Long id,
                              @NotBlank String nome,
                              @NotBlank String email) {
}
