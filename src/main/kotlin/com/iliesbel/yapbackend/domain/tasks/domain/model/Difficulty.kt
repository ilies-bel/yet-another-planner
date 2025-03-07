package com.iliesbel.yapbackend.domain.tasks.domain.model


enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    ;

    companion object {
        fun fromScore(difficultyScore: Long): Difficulty {
            if (difficultyScore < 30) {
                return EASY
            }

            if (difficultyScore < 70) {
                return MEDIUM
            }


            return HARD
        }
    }
}