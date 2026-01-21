package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
}
