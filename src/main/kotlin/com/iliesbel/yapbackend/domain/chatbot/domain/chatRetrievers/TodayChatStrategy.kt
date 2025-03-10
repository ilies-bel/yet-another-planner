package com.iliesbel.yapbackend.domain.chatbot.domain.chatRetrievers

import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat

interface TodayChatStrategy {
    val strategyName: ChatStrategy

    fun findTodayChat(): Chat
}