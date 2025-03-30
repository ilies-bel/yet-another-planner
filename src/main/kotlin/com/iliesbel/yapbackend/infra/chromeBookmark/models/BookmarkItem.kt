package com.iliesbel.yapbackend.infra.chromeBookmark.models

import com.iliesbel.yapbackend.infra.chromeBookmark.jacksonTimestampDeserializer.EpochToLocalDateTime
import java.time.LocalDateTime


data class BookmarkItem(
    val id: Long,
    val index: Int,
    @EpochToLocalDateTime
    val dateAdded: LocalDateTime,
    @EpochToLocalDateTime
    val dateLastUsed: LocalDateTime?,
    val title: String,
    val parentId: String?,
    val url: String?,
    val children: List<BookmarkItem>?
) {
    private val isLeaf = url != null

    fun naturalId(): String {
        return this.url!!
    }

    fun getAllLeafBookmarks(): List<BookmarkItem> {
        val result = mutableListOf<BookmarkItem>()

        this.children?.forEach { bookmark ->
            if (bookmark.isLeaf) {
                result.add(bookmark)
            }

            // If it has children, recursively process them
            bookmark.let { children ->
                result.addAll(children.getAllLeafBookmarks())
            }
        }


        return result
    }
}

