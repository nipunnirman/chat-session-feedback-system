package com.hSenid.assigmnent.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

enum class FeedbackStatus {
    PENDING, RESPONDED, EXPIRED
}

@Document(collection = "feedback_requests")
data class FeedbackRequest(
    @Id
    val id: String? = null,

    // Unique token sent to customer via link
    val feedbackId: String,

    val enterpriseId: String,
    val channel: String,

    var status: FeedbackStatus = FeedbackStatus.PENDING,
    var rating: Int? = null,
    var respondedAt: Instant? = null,

    val expiresAt: Instant,
    val createdAt: Instant = Instant.now()
)
