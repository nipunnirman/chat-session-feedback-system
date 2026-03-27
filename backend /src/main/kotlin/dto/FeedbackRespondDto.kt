package com.hSenid.assigmnent.backend.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class FeedbackRespondRequest(
    @field:NotNull(message = "rating is required")
    @field:Min(value = 1, message = "rating must be at least 1")
    @field:Max(value = 5, message = "rating must be at most 5")
    val rating: Int?
)

data class FeedbackRespondResponse(
    val status: String,
    val message: String
)

data class FeedbackRequestInfoResponse(
    val feedbackId: String,
    val enterpriseId: String,
    val status: String,
    val formConfig: FeedbackFormResponse?
)
