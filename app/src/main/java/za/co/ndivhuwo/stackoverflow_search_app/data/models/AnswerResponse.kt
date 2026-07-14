package za.co.ndivhuwo.stackoverflow_search_app.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerResponse(
    val items: List<Answer>,
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("quota_max") val quotaMax: Int,
    @SerialName("quota_remaining") val quotaRemaining: Int
)

@Serializable
data class Answer(
    @SerialName("answer_id") val answerId: Long,
    @SerialName("question_id") val questionId: Long,
    val body: String? = null,
    val owner: Owner,
    @SerialName("is_accepted") val isAccepted: Boolean,
    val score: Int,
    @SerialName("creation_date") val creationDate: Long,
    @SerialName("last_activity_date") val lastActivityDate: Long,
    @SerialName("last_edit_date") val lastEditDate: Long? = null
)
