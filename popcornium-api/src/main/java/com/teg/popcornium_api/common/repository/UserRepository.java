package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
