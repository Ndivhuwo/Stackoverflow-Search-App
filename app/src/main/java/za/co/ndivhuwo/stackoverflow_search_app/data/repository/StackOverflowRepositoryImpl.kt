package za.co.ndivhuwo.stackoverflow_search_app.data.repository

import za.co.ndivhuwo.stackoverflow_search_app.data.api.StackOverflowApi
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse
import javax.inject.Inject

class StackOverflowRepositoryImpl @Inject constructor(
    private val api: StackOverflowApi
) : StackOverflowRepository {
    override suspend fun searchQuestions(query: String): Result<StackOverflowResponse> {
        return try {
            val response = api.searchQuestions(title = query)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
