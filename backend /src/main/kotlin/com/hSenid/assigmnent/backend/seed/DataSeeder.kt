package com.hSenid.assigmnent.backend.seed

import com.hSenid.assigmnent.backend.model.FeedbackForm
import com.hSenid.assigmnent.backend.model.FeedbackRequest
import com.hSenid.assigmnent.backend.model.FeedbackStatus
import com.hSenid.assigmnent.backend.repository.FeedbackFormRepository
import com.hSenid.assigmnent.backend.repository.FeedbackRequestRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
@Profile("!test") // Don't run seeder during tests
class DataSeeder(
    private val feedbackFormRepository: FeedbackFormRepository,
    private val feedbackRequestRepository: FeedbackRequestRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        seedFeedbackForm()
        seedHSenidMobileForm()
        seedFeedbackRequests()
        seedHSenidMobileFeedbackRequests()
        println("=== Demo data seeded successfully ===")
        println("")
        println("--- enterprise-001 ---")
        println("Valid link    : http://localhost:5174/feedback/feedback-valid-001")
        println("Expired link  : http://localhost:5174/feedback/feedback-expired-001")
        println("Responded link: http://localhost:5174/feedback/feedback-done-001")
        println("")
        println("--- hsenid-mobile ---")
        println("Valid link    : http://localhost:5174/feedback/hsenid-valid-001")
        println("Expired link  : http://localhost:5174/feedback/hsenid-expired-001")
        println("Responded link: http://localhost:5174/feedback/hsenid-done-001")
    }

    private fun seedFeedbackForm() {
        if (!feedbackFormRepository.existsByEnterpriseId("enterprise-001")) {
            feedbackFormRepository.save(
                FeedbackForm(
                    enterpriseId = "enterprise-001",
                    headerText = "How was your experience?",
                    headerDescription = "We'd love to hear your feedback about our support chat.",
                    footerText = "Thank you for helping us improve!",
                    ratingLabels = listOf("Very Poor", "Poor", "Average", "Good", "Excellent"),
                    thankYouText = "Your feedback has been submitted. Thank you!",
                    invalidReplyText = "Please select a rating between 1 and 5.",
                    expiredReplyText = "Sorry, this feedback link has expired.",
                    skipForChannels = listOf("INSTAGRAM")
                )
            )
        }
    }

    private fun seedFeedbackRequests() {
        val now = Instant.now()

        // 1. Valid pending feedback (expires 24h from now)
        if (feedbackRequestRepository.findByFeedbackId("feedback-valid-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "feedback-valid-001",
                    enterpriseId = "enterprise-001",
                    channel = "WHATSAPP",
                    status = FeedbackStatus.PENDING,
                    expiresAt = now.plus(24, ChronoUnit.HOURS)
                )
            )
        }

        // 2. Expired feedback (expired 2 days ago)
        if (feedbackRequestRepository.findByFeedbackId("feedback-expired-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "feedback-expired-001",
                    enterpriseId = "enterprise-001",
                    channel = "MESSENGER",
                    status = FeedbackStatus.EXPIRED,
                    expiresAt = now.minus(2, ChronoUnit.DAYS)
                )
            )
        }

        // 3. Already responded feedback
        if (feedbackRequestRepository.findByFeedbackId("feedback-done-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "feedback-done-001",
                    enterpriseId = "enterprise-001",
                    channel = "WEB",
                    status = FeedbackStatus.RESPONDED,
                    rating = 4,
                    respondedAt = now.minus(1, ChronoUnit.HOURS),
                    expiresAt = now.plus(24, ChronoUnit.HOURS)
                )
            )
        }
    }

    // ── hSenid Mobile ──────────────────────────────────────────────────────

    private fun seedHSenidMobileForm() {
        if (!feedbackFormRepository.existsByEnterpriseId("hsenid-mobile")) {
            feedbackFormRepository.save(
                FeedbackForm(
                    enterpriseId = "hsenid-mobile",
                    headerText = "How was your hSenid Mobile support experience?",
                    headerDescription = "We'd love to hear your feedback about our chat support team.",
                    footerText = "Thank you for choosing hSenid Mobile!",
                    ratingLabels = listOf("Very Poor", "Poor", "Average", "Good", "Excellent"),
                    thankYouText = "Your feedback has been received. Thank you! 🙏",
                    invalidReplyText = "Please select a rating between 1 and 5.",
                    expiredReplyText = "Sorry, this feedback link has expired.",
                    skipForChannels = listOf()
                )
            )
        }
    }

    private fun seedHSenidMobileFeedbackRequests() {
        val now = Instant.now()

        // 1. Valid — customer can still rate
        if (feedbackRequestRepository.findByFeedbackId("hsenid-valid-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "hsenid-valid-001",
                    enterpriseId = "hsenid-mobile",
                    channel = "WHATSAPP",
                    status = FeedbackStatus.PENDING,
                    expiresAt = now.plus(24, ChronoUnit.HOURS)
                )
            )
        }

        // 2. Expired
        if (feedbackRequestRepository.findByFeedbackId("hsenid-expired-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "hsenid-expired-001",
                    enterpriseId = "hsenid-mobile",
                    channel = "MESSENGER",
                    status = FeedbackStatus.EXPIRED,
                    expiresAt = now.minus(2, ChronoUnit.DAYS)
                )
            )
        }

        // 3. Already responded
        if (feedbackRequestRepository.findByFeedbackId("hsenid-done-001").isEmpty) {
            feedbackRequestRepository.save(
                FeedbackRequest(
                    feedbackId = "hsenid-done-001",
                    enterpriseId = "hsenid-mobile",
                    channel = "WEB",
                    status = FeedbackStatus.RESPONDED,
                    rating = 5,
                    respondedAt = now.minus(30, ChronoUnit.MINUTES),
                    expiresAt = now.plus(24, ChronoUnit.HOURS)
                )
            )
        }
    }
}
