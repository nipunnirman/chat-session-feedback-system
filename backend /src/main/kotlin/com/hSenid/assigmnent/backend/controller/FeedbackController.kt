package com.hSenid.assigmnent.backend.controller

import com.hSenid.assigmnent.backend.dto.FeedbackRequestInfoResponse
import com.hSenid.assigmnent.backend.dto.FeedbackRespondRequest
import com.hSenid.assigmnent.backend.dto.FeedbackRespondResponse
import com.hSenid.assigmnent.backend.service.FeedbackService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/public/feedback/{feedbackId}")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:5173", "http://localhost:5174"])
class FeedbackController(private val feedbackService: FeedbackService) {

    /**
     * GET /api/public/feedback/{feedbackId}
     * Returns feedback request info + enterprise form config for the public page.
     */
    @GetMapping
    fun getFeedbackInfo(
        @PathVariable feedbackId: String
    ): ResponseEntity<FeedbackRequestInfoResponse> {
        val response = feedbackService.getFeedbackInfo(feedbackId)
        return ResponseEntity.ok(response)
    }

    /**
     * POST /api/public/feedback/{feedbackId}/respond
     * Submits a rating (1–5) for the given feedback request.
     */
    @PostMapping("/respond")
    fun respond(
        @PathVariable feedbackId: String,
        @Valid @RequestBody request: FeedbackRespondRequest
    ): ResponseEntity<FeedbackRespondResponse> {
        val response = feedbackService.submitRating(feedbackId, request.rating!!)
        return ResponseEntity.ok(response)
    }
}
