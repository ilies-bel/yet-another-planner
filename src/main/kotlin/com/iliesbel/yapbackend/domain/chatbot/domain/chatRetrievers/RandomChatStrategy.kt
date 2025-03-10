package com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers

import com.iliesbel.yapbackend.domain.chatbot.persistence.ChatRepository
import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat
import org.springframework.stereotype.Component

@Component
class RandomChatStrategy(private val chatRepository: ChatRepository) : TodayChatStrategy {
    override val strategyName: ChatStrategy
        get() = ChatStrategy.RANDOM

    override fun findTodayChat(): Chat {
        val foundChat = chatRepository.findRandom() ?: throw IllegalStateException("No chat found")
        return foundChat.let {
            Chat(
                id = it.id,
                message = it.message
            )
        }
    }
}