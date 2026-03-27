package com.hSenid.assigmnent.backend.controller

import com.hSenid.assigmnent.backend.dto.FeedbackFormRequest
import com.hSenid.assigmnent.backend.dto.FeedbackFormResponse
import com.hSenid.assigmnent.backend.model.FeedbackRequest
import com.hSenid.assigmnent.backend.service.AdminService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/enterprises/{enterpriseId}")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:5173", "http://localhost:5174"])
class AdminController(private val adminService: AdminService) {

    /**
     * GET /api/admin/enterprises/{enterpriseId}/session-feedback-form
     */
    @GetMapping("/session-feedback-form")
    fun getFormConfig(@PathVariable enterpriseId: String): ResponseEntity<FeedbackFormResponse> {
        return ResponseEntity.ok(adminService.getFormConfig(enterpriseId))
    }

    /**
     * PUT /api/admin/enterprises/{enterpriseId}/session-feedback-form
     */
    @PutMapping("/session-feedback-form")
    fun upsertFormConfig(
        @PathVariable enterpriseId: String,
        @Valid @RequestBody request: FeedbackFormRequest
    ): ResponseEntity<FeedbackFormResponse> {
        return ResponseEntity.ok(adminService.upsertFormConfig(enterpriseId, request))
    }

    /**
     * POST /api/admin/enterprises/{enterpriseId}/create-feedback-link?channel=WHATSAPP
     *
     * hSenid Mobile calls this when a customer's chat ends.
     * Returns a unique link to send to THAT specific customer.
     * Every call creates a NEW unique link — so different customers get different links.
     */
    @PostMapping("/create-feedback-link")
    fun createFeedbackLink(
        @PathVariable enterpriseId: String,
        @RequestParam(defaultValue = "WHATSAPP") channel: String
    ): ResponseEntity<Map<String, String>> {
        val request = adminService.createFeedbackLink(enterpriseId, channel)
        val link = "http://localhost:5174/feedback/${request.feedbackId}"
        return ResponseEntity.ok(mapOf(
            "feedbackId" to request.feedbackId,
            "link" to link,
            "channel" to request.channel,
            "expiresAt" to request.expiresAt.toString(),
            "instruction" to "Send this link to the customer via ${request.channel}"
        ))
    }

    /**
     * GET /api/admin/enterprises/{enterpriseId}/responses
     * Shows all feedback requests so the company can see customer ratings.
     */
    @GetMapping("/responses")
    fun getFeedbackResponses(@PathVariable enterpriseId: String): ResponseEntity<List<FeedbackRequest>> {
        return ResponseEntity.ok(adminService.getFeedbackResponses(enterpriseId))
    }
}
