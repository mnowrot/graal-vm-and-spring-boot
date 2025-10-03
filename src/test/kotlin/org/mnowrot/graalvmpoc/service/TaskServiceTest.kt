package org.mnowrot.graalvmpoc.service

import org.mnowrot.graalvmpoc.domain.TaskCreateRequest
import org.mnowrot.graalvmpoc.domain.TaskUpdateRequest
import kotlin.test.*

class TaskServiceTest {

    private lateinit var service: TaskService

    @BeforeTest
    fun setup() {
        service = TaskService()
        service.clear()
    }

    @Test
    fun `list returns empty then non-empty in order`() {
        assertTrue(service.list().isEmpty())
        val t1 = service.create(TaskCreateRequest(title = "A"))
        val t2 = service.create(TaskCreateRequest(title = "B"))
        val list = service.list()
        assertEquals(listOf(t1, t2), list)
    }

    @Test
    fun `get returns null when missing and task when present`() {
        assertNull(service.get(123))
        val created = service.create(TaskCreateRequest("Title"))
        assertEquals(created, service.get(created.id))
    }

    @Test
    fun `create sets fields and increments id`() {
        val t1 = service.create(TaskCreateRequest("T1", "D1"))
        val t2 = service.create(TaskCreateRequest("T2"))
        assertEquals(1, t1.id)
        assertEquals(2, t2.id)
        assertEquals("T1", t1.title)
        assertEquals("D1", t1.description)
        assertFalse(t1.completed)
        assertTrue(t1.createdAt <= t1.updatedAt)
    }

    @Test
    fun `update returns null when missing`() {
        assertNull(service.update(999, TaskUpdateRequest(title = "x")))
    }

    @Test
    fun `update applies all provided changes and updates timestamp`() {
        val t = service.create(TaskCreateRequest("Old", "Desc"))
        val before = t.updatedAt
        val updated = service.update(t.id, TaskUpdateRequest(title = "New", description = "NewDesc", completed = true))
        assertNotNull(updated)
        assertEquals("New", updated.title)
        assertEquals("NewDesc", updated.description)
        assertTrue(updated.completed)
        assertTrue(updated.updatedAt >= before)
    }

    @Test
    fun `update no-op leaves fields and logs no change`() {
        val t = service.create(TaskCreateRequest("Title", "Desc"))
        val beforeUpdated = t.updatedAt
        val beforeTitle = t.title
        val beforeDesc = t.description
        val beforeCompleted = t.completed
        val res = service.update(t.id, TaskUpdateRequest())
        assertNotNull(res)
        assertEquals(beforeTitle, res.title)
        assertEquals(beforeDesc, res.description)
        assertEquals(beforeCompleted, res.completed)
        assertEquals(beforeUpdated, res.updatedAt)
    }

    @Test
    fun `delete returns true when existing and false when missing`() {
        val t = service.create(TaskCreateRequest("T"))
        assertTrue(service.delete(t.id))
        assertFalse(service.delete(t.id))
    }

    @Test
    fun `clear empties storage and resets id sequence`() {
        service.create(TaskCreateRequest("A"))
        service.create(TaskCreateRequest("B"))
        service.clear()
        assertTrue(service.list().isEmpty())
        val t = service.create(TaskCreateRequest("C"))
        assertEquals(1, t.id)
    }
}
