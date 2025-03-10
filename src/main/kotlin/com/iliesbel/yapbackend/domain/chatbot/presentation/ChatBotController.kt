package com.iliesbel.yapbackend.domain.chatbot.presentation

import com.iliesbel.yapbackend.domain.chatbot.domain.ChatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatBotController(private val chatService: ChatService) {

    @GetMapping("/chats/current")
    fun getTodayChat(): Chat {
        return chatService.findTodayChat()
    }
}

data class Chat(
    val id: Long,
    val message: String,
)
