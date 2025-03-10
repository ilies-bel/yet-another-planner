package com.iliesbel.yapbackend.domain.chatbot.domain

import com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers.ChatStrategy
import com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers.TodayChatStrategy
import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat
import org.springframework.stereotype.Service

@Service
class ChatService(chatRetriever: List<TodayChatStrategy>) {

    private val strategyMap = chatRetriever.associateBy(TodayChatStrategy::strategyName)

    fun findTodayChat(): Chat {

        val strategy = strategyMap[ChatStrategy.RANDOM]
            ?: throw IllegalStateException("No strategy found for ${ChatStrategy.RANDOM}")


        return strategy.findTodayChat()
    }


}


