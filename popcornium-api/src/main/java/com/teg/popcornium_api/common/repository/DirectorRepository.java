package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, String> {
    Optional<Director> findByName(String s);
}
