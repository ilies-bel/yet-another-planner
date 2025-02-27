package com.iliesbel.yapbackend.domain.tasks.domain

@JvmInline
value class Difficulty(private val value: Int) {
    init {
        require(value > 0)
    }
}