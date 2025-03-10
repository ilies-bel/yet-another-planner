package com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers

import com.iliesbel.yapbackend.domain.chatbot.persistence.ChatRepository
import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat
import com.iliesbel.yapbackend.domain.users.persistence.UserRepository
import org.springframework.stereotype.Component

@Component
class RandomChatStrategy(private val chatRepository: ChatRepository) : TodayChatStrategy {
    override val strategyName: ChatStrategy
        get() = ChatStrategy.RANDOM

    override fun findTodayChat(): Chat {
        val foundChat = chatRepository.findRandom() ?: throw IllegalStateException("No chat found")
        return foundChat
    }
}


@Component
class TailoredChatStrategy(private val chatRepository: ChatRepository, private val userRepository: UserRepository) :
    TodayChatStrategy {
    override val strategyName: ChatStrategy
        get() = ChatStrategy.TAILORED

    override fun findTodayChat(): Chat {
        val user = userRepository.getCurrentUser()

        val foundChat = chatRepository.findRandomByScoreOverAndPersonalityScoreBetween(
            (user.loveScore - 50).score,
            (user.loveScore + 10).score,
            (user.personalityScore - 10).score,
            (user.personalityScore + 10).score,
        ) ?: throw NoSuchElementException("No chat found")

        return foundChat
    }
}