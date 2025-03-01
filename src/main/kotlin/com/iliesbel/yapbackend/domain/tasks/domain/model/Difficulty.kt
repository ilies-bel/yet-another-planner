package com.iliesbel.yapbackend.domain.tasks.domain.model

@JvmInline
value class Difficulty(private val value: Int) {
    init {
        require(value > 0)
    }
}