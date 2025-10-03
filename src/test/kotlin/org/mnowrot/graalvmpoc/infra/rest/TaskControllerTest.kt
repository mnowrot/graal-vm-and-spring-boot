package org.mnowrot.graalvmpoc.infra.rest

import org.mnowrot.graalvmpoc.domain.TaskCreateRequest
import org.mnowrot.graalvmpoc.domain.TaskUpdateRequest
import org.mnowrot.graalvmpoc.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskControllerTest {

    private lateinit var service: TaskService
    private lateinit var controller: TaskController

    @BeforeTest
    fun setup() {
        service = TaskService()
        service.clear()
        controller = TaskController(service)
    }

    @Test
    fun `list and create endpoints`() {
        assertTrue(controller.list().isEmpty())
        val created = controller.create(TaskCreateRequest("Title", "Desc"))
        assertEquals(1, created.id)
        assertEquals(1, controller.list().size)
    }

    @Test
    fun `get returns task when exists and 404 when missing`() {
        val t = controller.create(TaskCreateRequest("T"))
        assertEquals(t, controller.get(t.id))
        val ex = assertFailsWith<ResponseStatusException> { controller.get(999) }
        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(ex.statusCode.value()))
    }

    @Test
    fun `update returns 200 for existing and 404 when missing`() {
        val t = controller.create(TaskCreateRequest("T"))
        val updated = controller.update(t.id, TaskUpdateRequest(title = "N"))
        assertEquals("N", updated.title)
        val ex = assertFailsWith<ResponseStatusException> { controller.update(999, TaskUpdateRequest(title = "X")) }
        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(ex.statusCode.value()))
    }

    @Test
    fun `patch complete true and false and 404`() {
        val t = controller.create(TaskCreateRequest("T"))
        val completed = controller.setCompleted(t.id, true)
        assertTrue(completed.completed)
        val uncompleted = controller.setCompleted(t.id, false)
        assertFalse(uncompleted.completed)
        val ex = assertFailsWith<ResponseStatusException> { controller.setCompleted(999, true) }
        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(ex.statusCode.value()))
    }

    @Test
    fun `delete returns 204 when deleted and 404 when missing`() {
        val t = controller.create(TaskCreateRequest("T"))
        val resp = controller.delete(t.id)
        assertEquals(204, resp.statusCode.value())
        val resp404 = controller.delete(t.id)
        assertEquals(404, resp404.statusCode.value())
    }
}
