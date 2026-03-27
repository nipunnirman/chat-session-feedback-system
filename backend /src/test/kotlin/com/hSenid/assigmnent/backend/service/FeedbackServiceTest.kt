package com.hSenid.assigmnent.backend.service

import com.hSenid.assigmnent.backend.exception.AlreadyRespondedException
import com.hSenid.assigmnent.backend.exception.FeedbackExpiredException
import com.hSenid.assigmnent.backend.exception.ResourceNotFoundException
import com.hSenid.assigmnent.backend.model.FeedbackRequest
import com.hSenid.assigmnent.backend.model.FeedbackStatus
import com.hSenid.assigmnent.backend.repository.FeedbackFormRepository
import com.hSenid.assigmnent.backend.repository.FeedbackRequestRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional

class FeedbackServiceTest {

    private lateinit var feedbackRequestRepository: FeedbackRequestRepository
    private lateinit var feedbackFormRepository: FeedbackFormRepository
    private lateinit var feedbackService: FeedbackService

    @BeforeEach
    fun setUp() {
        feedbackRequestRepository = mock(FeedbackRequestRepository::class.java)
        feedbackFormRepository = mock(FeedbackFormRepository::class.java)
        feedbackService = FeedbackService(feedbackRequestRepository, feedbackFormRepository)
    }

    // ── submitRating: happy path ─────────────────────────────────────────────

    @Test
    fun `submitRating succeeds for valid pending request`() {
        val request = buildRequest(FeedbackStatus.PENDING, expiresInFuture = true)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-1")).thenReturn(Optional.of(request))
        `when`(feedbackRequestRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackRequest }

        val result = feedbackService.submitRating("fb-1", 4)

        assertEquals("success", result.status)
        assertEquals(FeedbackStatus.RESPONDED, request.status)
        assertEquals(4, request.rating)
        assertNotNull(request.respondedAt)
    }

    @Test
    fun `submitRating saves updated status to repository`() {
        val request = buildRequest(FeedbackStatus.PENDING, expiresInFuture = true)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-1")).thenReturn(Optional.of(request))
        `when`(feedbackRequestRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackRequest }

        feedbackService.submitRating("fb-1", 5)

        verify(feedbackRequestRepository).save(any())
    }

    // ── submitRating: already responded ─────────────────────────────────────

    @Test
    fun `submitRating throws AlreadyRespondedException when already responded`() {
        val request = buildRequest(FeedbackStatus.RESPONDED, expiresInFuture = true)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-done")).thenReturn(Optional.of(request))

        assertThrows<AlreadyRespondedException> {
            feedbackService.submitRating("fb-done", 3)
        }
    }

    // ── submitRating: expired ────────────────────────────────────────────────

    @Test
    fun `submitRating throws FeedbackExpiredException for explicitly expired request`() {
        val request = buildRequest(FeedbackStatus.EXPIRED, expiresInFuture = false)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-exp")).thenReturn(Optional.of(request))

        assertThrows<FeedbackExpiredException> {
            feedbackService.submitRating("fb-exp", 2)
        }
    }

    @Test
    fun `submitRating marks PENDING request as expired when expiry time has passed`() {
        // Status is still PENDING but expiresAt is in the past
        val request = buildRequest(FeedbackStatus.PENDING, expiresInFuture = false)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-late")).thenReturn(Optional.of(request))
        `when`(feedbackRequestRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackRequest }

        assertThrows<FeedbackExpiredException> {
            feedbackService.submitRating("fb-late", 3)
        }
        // Verify the status was updated to EXPIRED in DB
        assertEquals(FeedbackStatus.EXPIRED, request.status)
    }

    // ── submitRating: not found ──────────────────────────────────────────────

    @Test
    fun `submitRating throws ResourceNotFoundException for unknown feedbackId`() {
        `when`(feedbackRequestRepository.findByFeedbackId("unknown")).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            feedbackService.submitRating("unknown", 3)
        }
    }

    // ── getFeedbackInfo ──────────────────────────────────────────────────────

    @Test
    fun `getFeedbackInfo returns correct status for pending request`() {
        val request = buildRequest(FeedbackStatus.PENDING, expiresInFuture = true)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-1")).thenReturn(Optional.of(request))
        `when`(feedbackFormRepository.findByEnterpriseId(any())).thenReturn(Optional.empty())

        val result = feedbackService.getFeedbackInfo("fb-1")

        assertEquals("PENDING", result.status)
        assertEquals("fb-1", result.feedbackId)
    }

    @Test
    fun `getFeedbackInfo auto-expires past-due PENDING request`() {
        val request = buildRequest(FeedbackStatus.PENDING, expiresInFuture = false)
        `when`(feedbackRequestRepository.findByFeedbackId("fb-1")).thenReturn(Optional.of(request))
        `when`(feedbackRequestRepository.save(any())).thenAnswer { it.arguments[0] as FeedbackRequest }
        `when`(feedbackFormRepository.findByEnterpriseId(any())).thenReturn(Optional.empty())

        val result = feedbackService.getFeedbackInfo("fb-1")

        assertEquals("EXPIRED", result.status)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun buildRequest(status: FeedbackStatus, expiresInFuture: Boolean) = FeedbackRequest(
        id = "id-1",
        feedbackId = if (status == FeedbackStatus.RESPONDED) "fb-done"
                     else if (!expiresInFuture) "fb-late"
                     else "fb-1",
        enterpriseId = "ent-1",
        channel = "WHATSAPP",
        status = status,
        rating = if (status == FeedbackStatus.RESPONDED) 4 else null,
        expiresAt = if (expiresInFuture) Instant.now().plus(24, ChronoUnit.HOURS)
                    else Instant.now().minus(2, ChronoUnit.DAYS)
    )
}
