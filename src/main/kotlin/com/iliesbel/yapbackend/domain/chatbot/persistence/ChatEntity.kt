package com.iliesbel.yapbackend.domain.chatbot.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "chat_message")
class ChatEntity(
    @Id
    val id: Long,
    val message: String,
)