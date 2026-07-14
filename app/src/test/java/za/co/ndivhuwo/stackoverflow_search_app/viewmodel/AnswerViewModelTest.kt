package za.co.ndivhuwo.stackoverflow_search_app.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Answer
import za.co.ndivhuwo.stackoverflow_search_app.data.models.AnswerResponse
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AnswerViewModelTest {

    private val repository: StackOverflowRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when questionId is provided, then uiState is updated with answers`() = runTest {
        val questionId = 123L
        val mockResponse = AnswerResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        coEvery { repository.getAnswers(questionId) } returns Result.success(mockResponse)

        val savedStateHandle = SavedStateHandle(mapOf("questionId" to questionId))
        val viewModel = AnswerViewModel(repository, savedStateHandle)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(mockResponse.items, state.answers)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun `when fetchAnswers fails, then uiState has error`() = runTest {
        val questionId = 123L
        val errorMessage = "Network Error"
        coEvery { repository.getAnswers(questionId) } returns Result.failure(Exception(errorMessage))

        val savedStateHandle = SavedStateHandle(mapOf("questionId" to questionId))
        val viewModel = AnswerViewModel(repository, savedStateHandle)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun `when questionId is missing, then uiState remains initial`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AnswerViewModel(repository, savedStateHandle)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(emptyList<Answer>(), state.answers)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.error)
        }
    }
}
