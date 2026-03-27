package com.hSenid.assigmnent.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Allowed channel values
val ALLOWED_CHANNELS = setOf("WHATSAPP", "INSTAGRAM", "MESSENGER", "WEB")

data class FeedbackFormRequest(

    @field:NotBlank(message = "headerText is required")
    @field:Size(max = 200, message = "headerText must not exceed 200 characters")
    val headerText: String,

    @field:Size(max = 500, message = "headerDescription must not exceed 500 characters")
    val headerDescription: String? = null,

    @field:Size(max = 300, message = "footerText must not exceed 300 characters")
    val footerText: String? = null,

    // Must have exactly 5 non-blank labels
    @field:Size(min = 5, max = 5, message = "ratingLabels must have exactly 5 items")
    val ratingLabels: List<String>,

    @field:NotBlank(message = "thankYouText is required")
    @field:Size(max = 500, message = "thankYouText must not exceed 500 characters")
    val thankYouText: String,

    @field:NotBlank(message = "invalidReplyText is required")
    @field:Size(max = 300, message = "invalidReplyText must not exceed 300 characters")
    val invalidReplyText: String,

    @field:NotBlank(message = "expiredReplyText is required")
    @field:Size(max = 300, message = "expiredReplyText must not exceed 300 characters")
    val expiredReplyText: String,

    val skipForChannels: List<String> = emptyList()
)

data class FeedbackFormResponse(
    val enterpriseId: String,
    val headerText: String,
    val headerDescription: String?,
    val footerText: String?,
    val ratingLabels: List<String>,
    val thankYouText: String,
    val invalidReplyText: String,
    val expiredReplyText: String,
    val skipForChannels: List<String>
)
