package com.hSenid.assigmnent.backend.service

import com.hSenid.assigmnent.backend.dto.FeedbackFormResponse
import com.hSenid.assigmnent.backend.dto.FeedbackRequestInfoResponse
import com.hSenid.assigmnent.backend.dto.FeedbackRespondResponse
import com.hSenid.assigmnent.backend.exception.AlreadyRespondedException
import com.hSenid.assigmnent.backend.exception.FeedbackExpiredException
import com.hSenid.assigmnent.backend.exception.ResourceNotFoundException
import com.hSenid.assigmnent.backend.model.FeedbackForm
import com.hSenid.assigmnent.backend.model.FeedbackRequest
import com.hSenid.assigmnent.backend.model.FeedbackStatus
import com.hSenid.assigmnent.backend.repository.FeedbackFormRepository
import com.hSenid.assigmnent.backend.repository.FeedbackRequestRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FeedbackService(
    private val feedbackRequestRepository: FeedbackRequestRepository,
    private val feedbackFormRepository: FeedbackFormRepository
) {

    fun getFeedbackInfo(feedbackId: String): FeedbackRequestInfoResponse {
        val request = findFeedbackRequest(feedbackId)

        // Lazily mark expired on read
        val resolved = resolveExpiry(request)

        val formConfig = feedbackFormRepository.findByEnterpriseId(resolved.enterpriseId)
            .map { it.toResponse() }.orElse(null)

        return FeedbackRequestInfoResponse(
            feedbackId = resolved.feedbackId,
            enterpriseId = resolved.enterpriseId,
            status = resolved.status.name,
            formConfig = formConfig
        )
    }

    fun submitRating(feedbackId: String, rating: Int): FeedbackRespondResponse {
        val request = findFeedbackRequest(feedbackId)
        val resolved = resolveExpiry(request)

        when (resolved.status) {
            FeedbackStatus.EXPIRED -> throw FeedbackExpiredException("This feedback link has expired")
            FeedbackStatus.RESPONDED -> throw AlreadyRespondedException("Feedback has already been submitted")
            FeedbackStatus.PENDING -> Unit // proceed
        }

        resolved.status = FeedbackStatus.RESPONDED
        resolved.rating = rating
        resolved.respondedAt = Instant.now()
        feedbackRequestRepository.save(resolved)

        return FeedbackRespondResponse(
            status = "success",
            message = "Thank you for your feedback!"
        )
    }

    // If expiry time has passed and status is still PENDING, mark as EXPIRED and save
    private fun resolveExpiry(request: FeedbackRequest): FeedbackRequest {
        return if (request.status == FeedbackStatus.PENDING && Instant.now().isAfter(request.expiresAt)) {
            request.status = FeedbackStatus.EXPIRED
            feedbackRequestRepository.save(request)
        } else {
            request
        }
    }

    private fun findFeedbackRequest(feedbackId: String): FeedbackRequest =
        feedbackRequestRepository.findByFeedbackId(feedbackId)
            .orElseThrow { ResourceNotFoundException("Feedback request not found: $feedbackId") }

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
