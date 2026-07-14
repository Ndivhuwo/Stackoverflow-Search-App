package za.co.ndivhuwo.stackoverflow_search_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.QuestionItem
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.SearchUiState
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.SearchViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onQuestionClick: (Question) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onSearch = viewModel::search,
        onQuestionClick = onQuestionClick,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onQuestionClick: (Question) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search StackOverflow") },
            placeholder = { Text("Enter your query...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
                uiState.results.isEmpty() && uiState.query.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to StackOverflow Search!",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Type a message in the search bar above to start searching.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                uiState.results.isEmpty() && uiState.query.isNotEmpty() -> {
                    Text(
                        text = "No results found for \"${uiState.query}\"",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.results) { question ->
                            QuestionItem(
                                question = question,
                                onClick = { onQuestionClick(question) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    StackoverflowSearchAppTheme {
        HomeScreenContent(
            uiState = SearchUiState(),
            onQueryChanged = {},
            onSearch = {},
            onQuestionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenResultsPreview() {
    StackoverflowSearchAppTheme {
        HomeScreenContent(
            uiState = SearchUiState(
                query = "Kotlin Coroutines",
                results = listOf(
                    Question(
                        questionId = 1,
                        title = "How to see local variables values when debugging Kotlin Coroutines in IDEA?",
                        body = "When the debugger stops on breakpoint somewhere in Kotlin Coroutines code...",
                        link = "https://stackoverflow.com/questions/68728445",
                        tags = listOf("kotlin", "intellij-idea", "kotlin-coroutines"),
                        owner = Owner(displayName = "Nikita Tukkel"),
                        isAnswered = true,
                        viewCount = 5891,
                        answerCount = 2,
                        score = 12,
                        creationDate = 1628604773,
                        lastActivityDate = 1772824126
                    )
                )
            ),
            onQueryChanged = {},
            onSearch = {},
            onQuestionClick = {}
        )
    }
}
