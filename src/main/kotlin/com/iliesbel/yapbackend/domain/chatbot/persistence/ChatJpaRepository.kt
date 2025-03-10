package com.iliesbel.yapbackend.domain.chatbot.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface ChatJpaRepository : JpaRepository<ChatEntity, Long> {

    @Query(value = "select * from chat_message order by random() limit 1", nativeQuery = true)
    fun findRandom(): ChatEntity?
}