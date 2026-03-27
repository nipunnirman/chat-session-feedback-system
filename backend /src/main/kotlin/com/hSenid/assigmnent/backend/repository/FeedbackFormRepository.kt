package com.hSenid.assigmnent.backend.repository

import com.hSenid.assigmnent.backend.model.FeedbackForm
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FeedbackFormRepository : MongoRepository<FeedbackForm, String> {
    fun findByEnterpriseId(enterpriseId: String): Optional<FeedbackForm>
    fun existsByEnterpriseId(enterpriseId: String): Boolean
}
