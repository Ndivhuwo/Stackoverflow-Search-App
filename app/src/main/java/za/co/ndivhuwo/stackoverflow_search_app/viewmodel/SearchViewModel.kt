package za.co.ndivhuwo.stackoverflow_search_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepository
import za.co.ndivhuwo.stackoverflow_search_app.domain.AppError
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StackOverflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun search() {
        val currentQuery = _uiState.value.query
        if (currentQuery.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null, hasSearched = true) }

        viewModelScope.launch {
            repository.searchQuestions(currentQuery)
                .onSuccess { response ->
                    _uiState.update { 
                        it.copy(results = response.items, isLoading = false) 
                    }
                }
                .onFailure { error ->
                    val message = if (error is AppError) {
                        error.getDisplayMessage()
                    } else {
                        error.message ?: "Unknown error"
                    }
                    _uiState.update { 
                        it.copy(error = message, isLoading = false)
                    }
                }
        }
    }

    fun clearSearch() {
        _uiState.update { 
            it.copy(
                query = "",
                results = emptyList(),
                error = null,
                hasSearched = false
            )
        }
    }
}
