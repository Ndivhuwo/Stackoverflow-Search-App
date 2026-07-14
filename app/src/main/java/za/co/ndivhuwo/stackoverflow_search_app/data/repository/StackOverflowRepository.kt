package za.co.ndivhuwo.stackoverflow_search_app.data.repository

import za.co.ndivhuwo.stackoverflow_search_app.data.models.AnswerResponse
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse

interface StackOverflowRepository {
    suspend fun searchQuestions(query: String): Result<StackOverflowResponse>
    suspend fun getAnswers(questionId: Long): Result<AnswerResponse>
}
