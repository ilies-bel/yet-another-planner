package com.iliesbel.yapbackend.domain.chatbot.domain

data class PersonalityScore(val score: Int) {
    init {
        require(score in 0..100) { "Personality score must be between 0 and 100" }
    }

    operator fun minus(value: Int): PersonalityScore {
        return PersonalityScore((score - value).coerceIn(0, 100))
    }

    operator fun plus(value: Int): PersonalityScore {
        return PersonalityScore((score + value).coerceIn(0, 100))
    }

    companion object {
        val initial: PersonalityScore = PersonalityScore(50)
    }
}