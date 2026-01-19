package com.teg.popcornium_api.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String name, @Email String email, String password) {
}
