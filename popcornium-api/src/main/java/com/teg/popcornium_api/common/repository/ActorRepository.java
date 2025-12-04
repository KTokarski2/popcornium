package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {
    Optional<Actor> findByName(String s);
}
