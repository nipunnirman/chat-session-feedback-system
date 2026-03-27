package com.hSenid.assigmnent.backend.service

import com.hSenid.assigmnent.backend.dto.FeedbackFormRequest
import com.hSenid.assigmnent.backend.exception.ResourceNotFoundException
import com.hSenid.assigmnent.backend.exception.ValidationException
import com.hSenid.assigmnent.backend.model.FeedbackForm
import com.hSenid.assigmnent.backend.repository.FeedbackFormRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.Optional

class AdminServiceTest {

    private lateinit var feedbackFormRepository: FeedbackFormRepository
    private lateinit var adminService: AdminService

    private val validRequest = FeedbackFormRequest(
        headerText = "How was your experience?",
        headerDescription = "Please rate us.",
        footerText = "Thank you!",
        ratingLabels = listOf("Very Poor", "Poor", "Average", "Good", "Excellent"),
        thankYouText = "Thanks for your feedback!",
        invalidReplyText = "Invalid rating.",
        expiredReplyText = "This link has expired.",
        skipForChannels = listOf("WHATSAPP")
    )

    @BeforeEach
    fun setUp() {
        feedbackFormRepository = mock(FeedbackFormRepository::class.java)
        adminService = AdminService(feedbackFormRepository)
    }

    // ── GET tests ────────────────────────────────────────────────────────────

    @Test
    fun `getFormConfig throws ResourceNotFoundException when enterprise not found`() {
        `when`(feedbackFormRepository.findByEnterpriseId("unknown")).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> {
            adminService.getFormConfig("unknown")
        }
    }

    @Test
    fun `getFormConfig returns response when enterprise exists`() {
        val form = buildForm()
        `when`(feedbackFormRepository.findByEnterpriseId("ent-1")).thenReturn(Optional.of(form))

        val result = adminService.getFormConfig("ent-1")

        assertEquals("ent-1", result.enterpriseId)
        assertEquals("How was your experience?", result.headerText)
        assertEquals(5, result.ratingLabels.size)
    }

    // ── PUT / upsert tests ───────────────────────────────────────────────────

    @Test
    fun `upsertFormConfig creates new form when none exists`() {
        `when`(feedbackFormRepository.findByEnterpriseId("ent-new")).thenReturn(Optional.empty())
        `when`(feedbackFormRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackForm }

        val result = adminService.upsertFormConfig("ent-new", validRequest)

        assertEquals("ent-new", result.enterpriseId)
        verify(feedbackFormRepository).save(any())
    }

    @Test
    fun `upsertFormConfig updates existing form`() {
        val existing = buildForm("ent-1")
        `when`(feedbackFormRepository.findByEnterpriseId("ent-1")).thenReturn(Optional.of(existing))
        `when`(feedbackFormRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackForm }

        val updated = validRequest.copy(headerText = "Updated header")
        val result = adminService.upsertFormConfig("ent-1", updated)

        assertEquals("Updated header", result.headerText)
    }

    // ── Validation tests ─────────────────────────────────────────────────────

    @Test
    fun `upsertFormConfig throws ValidationException when ratingLabels contains blank`() {
        val bad = validRequest.copy(ratingLabels = listOf("Good", "", "Average", "Poor", "Excellent"))
        assertThrows<ValidationException> {
            adminService.upsertFormConfig("ent-1", bad)
        }
    }

    @Test
    fun `upsertFormConfig throws ValidationException when skipForChannels has duplicates`() {
        val bad = validRequest.copy(skipForChannels = listOf("WHATSAPP", "WHATSAPP"))
        assertThrows<ValidationException> {
            adminService.upsertFormConfig("ent-1", bad)
        }
    }

    @Test
    fun `upsertFormConfig throws ValidationException for invalid channel name`() {
        val bad = validRequest.copy(skipForChannels = listOf("TELEGRAM"))
        assertThrows<ValidationException> {
            adminService.upsertFormConfig("ent-1", bad)
        }
    }

    @Test
    fun `upsertFormConfig accepts empty skipForChannels`() {
        val req = validRequest.copy(skipForChannels = emptyList())
        `when`(feedbackFormRepository.findByEnterpriseId("ent-1")).thenReturn(Optional.empty())
        `when`(feedbackFormRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackForm }

        val result = adminService.upsertFormConfig("ent-1", req)
        assertTrue(result.skipForChannels.isEmpty())
    }

    @Test
    fun `upsertFormConfig accepts all four valid channels`() {
        val req = validRequest.copy(skipForChannels = listOf("WHATSAPP", "INSTAGRAM", "MESSENGER", "WEB"))
        `when`(feedbackFormRepository.findByEnterpriseId("ent-1")).thenReturn(Optional.empty())
        `when`(feedbackFormRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackForm }

        val result = adminService.upsertFormConfig("ent-1", req)
        assertEquals(4, result.skipForChannels.size)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun buildForm(enterpriseId: String = "ent-1") = FeedbackForm(
        id = "form-id-1",
        enterpriseId = enterpriseId,
        headerText = "How was your experience?",
        headerDescription = "Please rate us.",
        footerText = "Thank you!",
        ratingLabels = listOf("Very Poor", "Poor", "Average", "Good", "Excellent"),
        thankYouText = "Thanks for your feedback!",
        invalidReplyText = "Invalid rating.",
        expiredReplyText = "This link has expired.",
        skipForChannels = listOf("WHATSAPP")
    )
}
