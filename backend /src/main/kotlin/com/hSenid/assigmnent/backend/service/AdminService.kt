package com.hSenid.assigmnent.backend.service

import com.hSenid.assigmnent.backend.dto.ALLOWED_CHANNELS
import com.hSenid.assigmnent.backend.dto.FeedbackFormRequest
import com.hSenid.assigmnent.backend.dto.FeedbackFormResponse
import com.hSenid.assigmnent.backend.exception.ResourceNotFoundException
import com.hSenid.assigmnent.backend.exception.ValidationException
import com.hSenid.assigmnent.backend.model.FeedbackForm
import com.hSenid.assigmnent.backend.repository.FeedbackFormRepository
import org.springframework.stereotype.Service
import java.time.Instant

import com.hSenid.assigmnent.backend.model.FeedbackRequest
import com.hSenid.assigmnent.backend.model.FeedbackStatus
import com.hSenid.assigmnent.backend.repository.FeedbackRequestRepository
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class AdminService(
    private val feedbackFormRepository: FeedbackFormRepository,
    private val feedbackRequestRepository: FeedbackRequestRepository
) {

    /**
     * Creates a brand-new unique feedback link for ONE specific customer.
     * Call this every time a chat session ends.
     */
    fun createFeedbackLink(enterpriseId: String, channel: String): FeedbackRequest {
        val newRequest = FeedbackRequest(
            feedbackId = UUID.randomUUID().toString(),  // Unique ID for this one customer
            enterpriseId = enterpriseId,
            channel = channel.uppercase(),
            status = FeedbackStatus.PENDING,
            expiresAt = Instant.now().plus(24, ChronoUnit.HOURS)
        )
        return feedbackRequestRepository.save(newRequest)
    }

    /**
     * Returns all feedback requests for this enterprise (for the Responses dashboard).
     */
    fun getFeedbackResponses(enterpriseId: String): List<FeedbackRequest> {
        return feedbackRequestRepository.findAllByEnterpriseId(enterpriseId)
            .sortedByDescending { it.createdAt }
    }

    fun getFormConfig(enterpriseId: String): FeedbackFormResponse {
        val form = feedbackFormRepository.findByEnterpriseId(enterpriseId)
            .orElseThrow { ResourceNotFoundException("No feedback form configured for enterprise: $enterpriseId") }
        return form.toResponse()
    }

    fun upsertFormConfig(enterpriseId: String, request: FeedbackFormRequest): FeedbackFormResponse {
        // Manual validations beyond @Valid annotations
        validateFormRequest(request)

        val existing = feedbackFormRepository.findByEnterpriseId(enterpriseId).orElse(null)

        val form = if (existing != null) {
            existing.copy(
                headerText = request.headerText,
                headerDescription = request.headerDescription,
                footerText = request.footerText,
                ratingLabels = request.ratingLabels,
                thankYouText = request.thankYouText,
                invalidReplyText = request.invalidReplyText,
                expiredReplyText = request.expiredReplyText,
                skipForChannels = request.skipForChannels,
                updatedAt = Instant.now()
            )
        } else {
            FeedbackForm(
                enterpriseId = enterpriseId,
                headerText = request.headerText,
                headerDescription = request.headerDescription,
                footerText = request.footerText,
                ratingLabels = request.ratingLabels,
                thankYouText = request.thankYouText,
                invalidReplyText = request.invalidReplyText,
                expiredReplyText = request.expiredReplyText,
                skipForChannels = request.skipForChannels
            )
        }

        return feedbackFormRepository.save(form).toResponse()
    }

    private fun validateFormRequest(request: FeedbackFormRequest) {
        // ratingLabels must not contain blank entries
        if (request.ratingLabels.any { it.isBlank() }) {
            throw ValidationException("ratingLabels must not contain blank values")
        }

        // skipForChannels must not have duplicates
        if (request.skipForChannels.size != request.skipForChannels.toSet().size) {
            throw ValidationException("skipForChannels must not contain duplicate values")
        }

        // skipForChannels must only contain allowed channel names
        val invalidChannels = request.skipForChannels.filter { it.uppercase() !in ALLOWED_CHANNELS }
        if (invalidChannels.isNotEmpty()) {
            throw ValidationException(
                "Invalid channel(s): $invalidChannels. Allowed values: $ALLOWED_CHANNELS"
            )
        }
    }

    private fun FeedbackForm.toResponse() = FeedbackFormResponse(
        enterpriseId = enterpriseId,
        headerText = headerText,
        headerDescription = headerDescription,
        footerText = footerText,
        ratingLabels = ratingLabels,
        thankYouText = thankYouText,
        invalidReplyText = invalidReplyText,
        expiredReplyText = expiredReplyText,
        skipForChannels = skipForChannels
    )
}
