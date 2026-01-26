package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Conversation;
import com.teg.popcornium_api.common.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    List<Conversation> findByUserIdOrderByCreatedDesc(String userId);

    @Query("SELECT m FROM ConversationMessage m WHERE m.conversation = :conv ORDER BY m.created DESC LIMIT 10")
    List<ConversationMessage> findLast10(@Param("conv") Conversation conv);
}
