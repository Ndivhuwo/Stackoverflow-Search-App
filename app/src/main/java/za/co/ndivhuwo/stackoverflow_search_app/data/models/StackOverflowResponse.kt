package za.co.ndivhuwo.stackoverflow_search_app.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StackOverflowResponse(
    val items: List<Question>,
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("quota_max") val quotaMax: Int,
    @SerialName("quota_remaining") val quotaRemaining: Int
)

@Serializable
data class Question(
    @SerialName("question_id") val questionId: Long,
    val title: String,
    val body: String? = null,
    val link: String,
    val tags: List<String>,
    val owner: Owner,
    @SerialName("is_answered") val isAnswered: Boolean,
    @SerialName("view_count") val viewCount: Int,
    @SerialName("answer_count") val answerCount: Int,
    val score: Int,
    @SerialName("creation_date") val creationDate: Long,
    @SerialName("last_activity_date") val lastActivityDate: Long,
    @SerialName("last_edit_date") val lastEditDate: Long? = null
)

@Serializable
data class Owner(
    @SerialName("user_id") val userId: Long? = null,
    @SerialName("display_name") val displayName: String,
    @SerialName("profile_image") val profileImage: String? = null,
    val link: String? = null
)
