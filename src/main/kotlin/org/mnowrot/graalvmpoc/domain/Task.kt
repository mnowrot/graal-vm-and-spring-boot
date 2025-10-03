package org.mnowrot.graalvmpoc.domain

import java.time.Instant

/**
 * Simple in-memory Task representation.
 */
data class Task(
    val id: Long,
    var title: String,
    var description: String? = null,
    var completed: Boolean = false,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = createdAt
)

/** DTO used when creating a task */
data class TaskCreateRequest(
    val title: String,
    val description: String? = null
)

/** DTO used when updating a task */
data class TaskUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null
)
