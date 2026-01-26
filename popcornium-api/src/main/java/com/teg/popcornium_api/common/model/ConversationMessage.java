package com.teg.popcornium_api.common.model;

import com.teg.popcornium_api.common.model.types.MessageSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
public class ConversationMessage extends AbstractEntity{

    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_source")
    private MessageSource messageSource;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
}
