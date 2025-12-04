package com.teg.popcornium_api.common.model;

import lombok.Getter;

@Getter
public enum Language {
    PL("Polish"),
    EN("English");

    private final String fullName;

    Language(String fullName) {
        this.fullName = fullName;
    }
}