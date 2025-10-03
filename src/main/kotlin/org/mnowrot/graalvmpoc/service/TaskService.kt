package org.mnowrot.graalvmpoc.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.mnowrot.graalvmpoc.domain.Task
import org.mnowrot.graalvmpoc.domain.TaskCreateRequest
import org.mnowrot.graalvmpoc.domain.TaskUpdateRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class TaskService {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    private val idSequence = AtomicLong(0)
    private val storage = ConcurrentHashMap<Long, Task>()

    fun list(): List<Task> {
        val tasks = storage.values.sortedBy { it.id }
        log.info { "Listing tasks: count=${tasks.size}" }
        return tasks
    }

    fun get(id: Long): Task? {
        val task = storage[id]
        if (task == null) {
            log.info { "Get task: id=$id not found" }
        } else {
            log.info { "Get task: id=${task.id} title='${task.title}' completed=${task.completed}" }
        }
        return task
    }

    fun create(req: TaskCreateRequest): Task {
        val id = idSequence.incrementAndGet()
        val now = Instant.now()
        val task = Task(
            id = id,
            title = req.title,
            description = req.description,
            completed = false,
            createdAt = now,
            updatedAt = now
        )
        storage[id] = task
        log.info { "Created task id=$id title='${req.title}'" }
        return task
    }

    fun update(id: Long, req: TaskUpdateRequest): Task? {
        val task = storage[id]
        if (task == null) {
            log.info { "Update requested for id=$id but task not found" }
            return null
        }
        var changed = false
        req.title?.let { task.title = it; changed = true }
        req.description?.let { task.description = it; changed = true }
        req.completed?.let { task.completed = it; changed = true }
        if (changed) {
            task.updatedAt = Instant.now()
            log.info { "Updated task id=${task.id} title='${task.title}' completed=${task.completed}" }
        } else {
            log.info { "No changes applied for task id=${task.id}" }
        }
        return task
    }

    fun delete(id: Long): Boolean {
        val removed = storage.remove(id)
        val deleted = removed != null
        if (deleted) {
            log.info { "Deleted task id=$id" }
        } else {
            log.warn { "Delete requested for id=$id but task not found" }
        }
        return deleted
    }

    fun clear() {
        val count = storage.size
        storage.clear()
        idSequence.set(0)
        log.info { "Cleared in-memory storage, removed $count task(s); id sequence reset" }
    }
}