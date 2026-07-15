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
import za.co.ndivhuwo.stackoverflow_search_app.domain.AppError
import za.co.ndivhuwo.stackoverflow_search_app.util.AppLogger
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

    companion object {
        private const val TAG = "AnswerViewModel"
    }

    private val questionId: Long? = savedStateHandle["questionId"]

    private val _uiState = MutableStateFlow(AnswerUiState())
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()

    init {
        AppLogger.d(TAG, "Initializing with questionId: $questionId")
        questionId?.let { fetchAnswers(it) }
    }

    fun retry() {
        AppLogger.i(TAG, "User triggered retry for questionId: $questionId")
        questionId?.let { fetchAnswers(it) }
    }

    private fun fetchAnswers(id: Long) {
        AppLogger.d(TAG, "Fetching answers for id: $id")
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getAnswers(id)
                .onSuccess { response ->
                    AppLogger.d(TAG, "Successfully fetched ${response.items.size} answers")
                    _uiState.update { 
                        it.copy(answers = response.items, isLoading = false) 
                    }
                }
                .onFailure { error ->
                    AppLogger.e(TAG, "Failed to fetch answers for id: $id", error)
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
}
