package za.co.ndivhuwo.stackoverflow_search_app.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import za.co.ndivhuwo.stackoverflow_search_app.data.api.StackOverflowApi
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse

class StackOverflowRepositoryImplTest {

    private val api: StackOverflowApi = mockk()
    private lateinit var repository: StackOverflowRepositoryImpl

    @Before
    fun setup() {
        repository = StackOverflowRepositoryImpl(api)
    }

    @Test
    fun `when api search is successful, then returns success result`() = runTest {
        val query = "kotlin"
        val mockResponse = StackOverflowResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        coEvery { api.searchQuestions(title = query) } returns mockResponse

        val result = repository.searchQuestions(query)

        assertTrue(result.isSuccess)
        assertEquals(mockResponse, result.getOrNull())
    }

    @Test
    fun `when api search throws exception, then returns failure result`() = runTest {
        val query = "kotlin"
        val exception = RuntimeException("API Error")
        coEvery { api.searchQuestions(title = query) } throws exception

        val result = repository.searchQuestions(query)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `when getAnswers is successful, then returns success result`() = runTest {
        val questionId = 123L
        val mockResponse = za.co.ndivhuwo.stackoverflow_search_app.data.models.AnswerResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        coEvery { api.getAnswers(questionIds = questionId.toString()) } returns mockResponse

        val result = repository.getAnswers(questionId)

        assertTrue(result.isSuccess)
        assertEquals(mockResponse, result.getOrNull())
    }

    @Test
    fun `when getAnswers throws exception, then returns failure result`() = runTest {
        val questionId = 123L
        val exception = RuntimeException("API Error")
        coEvery { api.getAnswers(questionIds = questionId.toString()) } throws exception

        val result = repository.getAnswers(questionId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
