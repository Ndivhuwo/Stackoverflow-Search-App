package za.co.ndivhuwo.stackoverflow_search_app.viewmodel

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
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val repository: StackOverflowRepository = mockk()
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when query changed, then uiState is updated`() = runTest {
        val query = "kotlin"
        viewModel.onQueryChanged(query)
        assertEquals(query, viewModel.uiState.value.query)
    }

    @Test
    fun `when search is successful, then uiState has results`() = runTest {
        val query = "kotlin"
        val mockResponse = StackOverflowResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        coEvery { repository.searchQuestions(query) } returns Result.success(mockResponse)

        viewModel.onQueryChanged(query)
        
        viewModel.uiState.test {
            // Initial state
            assertEquals(query, awaitItem().query)
            
            viewModel.search()
            
            // Loading state
            val loadingItem = awaitItem()
            assertEquals(true, loadingItem.isLoading)
            
            // Success state
            val successItem = awaitItem()
            assertEquals(false, successItem.isLoading)
            assertEquals(mockResponse.items, successItem.results)
        }
    }

    @Test
    fun `when search fails, then uiState has error`() = runTest {
        val query = "kotlin"
        val errorMessage = "Network Error"
        coEvery { repository.searchQuestions(query) } returns Result.failure(Exception(errorMessage))

        viewModel.onQueryChanged(query)
        
        viewModel.uiState.test {
            awaitItem() // Skip initial
            
            viewModel.search()
            
            awaitItem() // Skip loading
            
            val errorItem = awaitItem()
            assertEquals(false, errorItem.isLoading)
            assertEquals(errorMessage, errorItem.error)
        }
    }
}
