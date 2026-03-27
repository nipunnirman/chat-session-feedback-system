package com.hSenid.assigmnent.backend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hSenid.assigmnent.backend.dto.FeedbackRespondRequest
import com.hSenid.assigmnent.backend.dto.FeedbackRespondResponse
import com.hSenid.assigmnent.backend.exception.AlreadyRespondedException
import com.hSenid.assigmnent.backend.exception.FeedbackExpiredException
import com.hSenid.assigmnent.backend.exception.ResourceNotFoundException
import com.hSenid.assigmnent.backend.service.FeedbackService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(FeedbackController::class)
class FeedbackControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var feedbackService: FeedbackService

    // ── POST /respond ────────────────────────────────────────────────────────

    @Test
    fun `respond returns 200 for valid rating`() {
        `when`(feedbackService.submitRating("fb-valid", 4))
            .thenReturn(FeedbackRespondResponse("success", "Thank you for your feedback!"))

        mockMvc.perform(
            post("/api/public/feedback/fb-valid/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(FeedbackRespondRequest(4)))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
    }

    @Test
    fun `respond returns 400 when rating is null`() {
        mockMvc.perform(
            post("/api/public/feedback/fb-valid/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": null}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `respond returns 400 when rating is below 1`() {
        mockMvc.perform(
            post("/api/public/feedback/fb-valid/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": 0}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `respond returns 400 when rating is above 5`() {
        mockMvc.perform(
            post("/api/public/feedback/fb-valid/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": 6}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `respond returns 404 for unknown feedbackId`() {
        `when`(feedbackService.submitRating("fb-unknown", 3))
            .thenThrow(ResourceNotFoundException("Feedback request not found"))

        mockMvc.perform(
            post("/api/public/feedback/fb-unknown/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": 3}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Not found"))
    }

    @Test
    fun `respond returns 409 when already responded`() {
        `when`(feedbackService.submitRating("fb-done", 3))
            .thenThrow(AlreadyRespondedException("Feedback already submitted"))

        mockMvc.perform(
            post("/api/public/feedback/fb-done/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": 3}""")
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error").value("Already responded"))
    }

    @Test
    fun `respond returns 410 when feedback is expired`() {
        `when`(feedbackService.submitRating("fb-exp", 3))
            .thenThrow(FeedbackExpiredException("Feedback link has expired"))

        mockMvc.perform(
            post("/api/public/feedback/fb-exp/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating": 3}""")
        )
            .andExpect(status().isGone)
            .andExpect(jsonPath("$.error").value("Expired"))
    }
}
