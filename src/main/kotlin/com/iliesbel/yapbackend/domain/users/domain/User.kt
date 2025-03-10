package com.iliesbel.yapbackend.domain.users.domain

import com.iliesbel.yapbackend.domain.chatbot.domain.LoveScore
import com.iliesbel.yapbackend.domain.chatbot.domain.PersonalityScore

data class User(
    val id: Long?,
    val name: String,
    val email: String,
    val personalityScore: PersonalityScore,
    val loveScore: LoveScore,
)