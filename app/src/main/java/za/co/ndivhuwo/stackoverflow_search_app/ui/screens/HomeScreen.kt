package za.co.ndivhuwo.stackoverflow_search_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import za.co.ndivhuwo.stackoverflow_search_app.R
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.ErrorView
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.QuestionItem
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.SearchUiState
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onQuestionClick: (Question) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stack_overflow_logo),
                        contentDescription = "Stack Overflow Logo",
                        modifier = Modifier.height(60.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Developed by",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ndivhuwo Nthambeleni",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF48024)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.stack_overflow_logo),
                            contentDescription = "Stack Overflow Logo",
                            modifier = Modifier.height(40.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                onQueryChanged = viewModel::onQueryChanged,
                onSearch = viewModel::search,
                onClearSearch = viewModel::clearSearch,
                onQuestionClick = onQuestionClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    uiState: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClearSearch: () -> Unit,
    onQuestionClick: (Question) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF48024))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search StackOverflow") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error,
                        onRetry = onSearch,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                !uiState.hasSearched -> {
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
                            text = "Type something and press search to get started.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                uiState.results.isEmpty() -> {
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
            onClearSearch = {},
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
            onClearSearch = {},
            onQuestionClick = {}
        )
    }
}
