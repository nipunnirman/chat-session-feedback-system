package com.hSenid.assigmnent.backend.repository

import com.hSenid.assigmnent.backend.model.FeedbackRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FeedbackRequestRepository : MongoRepository<FeedbackRequest, String> {
    fun findByFeedbackId(feedbackId: String): Optional<FeedbackRequest>
    fun findAllByEnterpriseId(enterpriseId: String): List<FeedbackRequest>
}
