package za.co.ndivhuwo.stackoverflow_search_app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Answer
import za.co.ndivhuwo.stackoverflow_search_app.data.repository.StackOverflowRepository
import javax.inject.Inject

data class AnswerUiState(
    val answers: List<Answer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val repository: StackOverflowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val questionId: Long? = savedStateHandle["questionId"]

    private val _uiState = MutableStateFlow(AnswerUiState())
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()

    init {
        questionId?.let { fetchAnswers(it) }
    }

    private fun fetchAnswers(id: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getAnswers(id)
                .onSuccess { response ->
                    _uiState.update { 
                        it.copy(answers = response.items, isLoading = false) 
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(error = error.message ?: "Unknown error", isLoading = false) 
                    }
                }
        }
    }
}
