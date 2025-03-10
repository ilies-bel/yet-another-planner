package com.iliesbel.yapbackend.domain.chatbot.persistence

import com.iliesbel.yapbackend.domain.chatbot.presentation.Chat
import org.springframework.stereotype.Repository

@Repository
class ChatRepository(private val chatJpaRepository: ChatJpaRepository) {
    fun findRandom(): Chat? {
        return chatJpaRepository.findRandom()?.let {
            Chat(
                id = it.id,
                message = it.message
            )
        }
    }

    fun findRandomByScoreOverAndPersonalityScoreBetween(
        loveScoreStart: Int,
        loveScoreEnd: Int,
        personalityStart: Int,
        personalityEnd: Int
    ): Chat? {
        return chatJpaRepository.findRandomByScoreOverAndPersonalityScoreBetween(
            loveScoreStart,
            loveScoreEnd,
            personalityStart,
            personalityEnd
        )?.let {
            Chat(
                id = it.id,
                message = it.message
            )
        }
    }
}