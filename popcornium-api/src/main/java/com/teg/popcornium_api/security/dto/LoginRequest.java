package com.teg.popcornium_api.security.dto;

import jakarta.validation.constraints.Email;

public record LoginRequest(@Email String email, String password) {
}
