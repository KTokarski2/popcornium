package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.User;

import java.util.Optional;

public interface CurrentUserService {
    User getCurrentUser();
    Optional<User> getCurrentUserOptional();
}
