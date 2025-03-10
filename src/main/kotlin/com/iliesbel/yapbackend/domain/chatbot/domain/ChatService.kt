package com.iliesbel.yapbackend.domain.chatbot.domain

import com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers.ChatStrategy
import com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers.TodayChatStrategy
import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat
import com.iliesbel.yapbackend.domain.users.persistence.UserRepository
import org.springframework.stereotype.Service

@Service
class ChatService(chatRetriever: List<TodayChatStrategy>, private val userRepository: UserRepository) {

    private val strategyMap = chatRetriever.associateBy(TodayChatStrategy::strategyName)

    fun findTodayChat(): Chat {

        val strategy = getStrategy() ?: throw NoSuchElementException("User not found")

        return strategy.findTodayChat()
    }


    private fun getStrategy(): TodayChatStrategy? {
        userRepository.findCurrentUser() ?: return strategyMap[ChatStrategy.RANDOM]

        return strategyMap[ChatStrategy.TAILORED]
    }
}


