package org.mnowrot.graalvmpoc.infra.rest

import org.mnowrot.graalvmpoc.domain.Task
import org.mnowrot.graalvmpoc.domain.TaskCreateRequest
import org.mnowrot.graalvmpoc.domain.TaskUpdateRequest
import org.mnowrot.graalvmpoc.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/tasks")
@Validated
class TaskController(private val service: TaskService) {

    @GetMapping
    fun list(): List<Task> = service.list()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Task =
        service.get(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task $id not found")

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: TaskCreateRequest): Task = service.create(req)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: TaskUpdateRequest): Task =
        service.update(id, req) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task $id not found")

    @PatchMapping("/{id}/complete")
    fun setCompleted(@PathVariable id: Long, @RequestParam completed: Boolean): Task =
        service.update(id, TaskUpdateRequest(completed = completed))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task $id not found")

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (service.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
}
