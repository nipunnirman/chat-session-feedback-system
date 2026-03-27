package com.hSenid.assigmnent.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "feedback_forms")
data class FeedbackForm(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val enterpriseId: String,

    val headerText: String,
    val headerDescription: String? = null,
    val footerText: String? = null,

    // Exactly 5 labels for ratings 1–5
    val ratingLabels: List<String>,

    val thankYouText: String,
    val invalidReplyText: String,
    val expiredReplyText: String,

    // Channel names to skip feedback for (e.g. ["WHATSAPP", "INSTAGRAM"])
    val skipForChannels: List<String> = emptyList(),

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
