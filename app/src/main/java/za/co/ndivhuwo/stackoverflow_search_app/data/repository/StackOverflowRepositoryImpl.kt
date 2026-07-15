package za.co.ndivhuwo.stackoverflow_search_app.data.repository

import za.co.ndivhuwo.stackoverflow_search_app.data.api.StackOverflowApi
import za.co.ndivhuwo.stackoverflow_search_app.data.models.AnswerResponse
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse
import za.co.ndivhuwo.stackoverflow_search_app.domain.AppError
import za.co.ndivhuwo.stackoverflow_search_app.util.AppLogger
import javax.inject.Inject

class StackOverflowRepositoryImpl @Inject constructor(
    private val api: StackOverflowApi
) : StackOverflowRepository {

    companion object {
        private const val TAG = "StackOverflowRepo"
    }

    override suspend fun searchQuestions(query: String): Result<StackOverflowResponse> {
        AppLogger.d(TAG, "searchQuestions called with query: $query")
        return try {
            val response = api.searchQuestions(title = query)
            AppLogger.d(TAG, "searchQuestions success: ${response.items.size} items found")
            Result.success(response)
        } catch (e: Exception) {
            AppLogger.e(TAG, "searchQuestions failure for query: $query", e)
            Result.failure(AppError.fromThrowable(e))
        }
    }

    override suspend fun getAnswers(questionId: Long): Result<AnswerResponse> {
        AppLogger.d(TAG, "getAnswers called for questionId: $questionId")
        return try {
            val response = api.getAnswers(questionIds = questionId.toString())
            AppLogger.d(TAG, "getAnswers success: ${response.items.size} answers found")
            Result.success(response)
        } catch (e: Exception) {
            AppLogger.e(TAG, "getAnswers failure for questionId: $questionId", e)
            Result.failure(AppError.fromThrowable(e))
        }
    }
}
