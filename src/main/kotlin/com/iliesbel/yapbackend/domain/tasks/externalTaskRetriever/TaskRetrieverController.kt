package com.iliesbel.yapbackend.domain.tasks.externalTaskRetriever

import com.iliesbel.yapbackend.infra.chromeBookmark.models.BookmarkItem
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskRetrieverController(private val externalTaskService: ExternalTaskService) {
    private val log = LoggerFactory.getLogger(TaskRetrieverController::class.java) // TODO find prefixing logger factory

    @PostMapping("/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    fun getBookmarks(@RequestBody bookmarkItem: BookmarkItem) {
        log.info("found " + bookmarkItem.getAllLeafBookmarks().size + " to save")

        return externalTaskService.save(
            bookmarkItem.getAllLeafBookmarks()
                .map {
                    ExternalTask(
                        naturalId = it.naturalId(),
                        name = it.title,
                        description = null,
                        url = it.url!!,
                        img64 = null,
                        tags = listOf(),
                    )
                }

        )
    }
}