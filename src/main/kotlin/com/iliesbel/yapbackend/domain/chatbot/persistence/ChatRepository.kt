package com.iliesbel.yapbackend.domain.chatbot.persistence

import org.springframework.stereotype.Repository

@Repository
class ChatRepository(private val chatJpaRepository: ChatJpaRepository) {
    fun findRandom(): ChatEntity? {
        return chatJpaRepository.findRandom()
    }
}