package com.iliesbel.yapbackend.domain.chatbot.domain

data class LoveScore(val score: Int) {
    init {
        require(score in 0..100) { "Love score must be between 0 and 100" }
    }

    operator fun minus(value: Int): LoveScore {
        return LoveScore((score - value).coerceIn(0, 100))
    }

    operator fun plus(value: Int): LoveScore {
        return LoveScore((score + value).coerceIn(0, 100))
    }

    companion object {
        val min: LoveScore = LoveScore(0)
        val initial: LoveScore = LoveScore(50)
    }
}