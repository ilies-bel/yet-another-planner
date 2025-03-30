package com.iliesbel.yapbackend.domain.tasks.externalTaskRetriever

data class ExternalTask(
    val naturalId: String,
    val name: String,
    val tags: List<String>?,
    val description: String?,
    val url: String?,
    val img64: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ExternalTask

        return naturalId == other.naturalId
    }

    override fun hashCode(): Int {
        return naturalId.hashCode()
    }
}


