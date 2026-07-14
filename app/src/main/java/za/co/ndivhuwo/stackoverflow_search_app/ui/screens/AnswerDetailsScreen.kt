package za.co.ndivhuwo.stackoverflow_search_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Answer
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.HtmlText
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.OwnerCard
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.TagChip
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.AnswerUiState
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.AnswerViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerDetailsScreen(
    question: Question,
    onBack: () -> Unit,
    viewModel: AnswerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Question Details", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        AnswerDetailsContent(
            question = question,
            uiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AnswerDetailsContent(
    question: Question,
    uiState: AnswerUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question Section
        item {
            Column {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF282828),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Asked ${formatDate(question.creationDate)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                question.body?.let {
                    HtmlText(
                        html = it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                if (question.tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(question.tags) { tag ->
                            TagChip(tag = tag)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OwnerCard(owner = question.owner)

            }
        }

        // Answers Header
        item {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${uiState.answers.size} Answers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Answers List
        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.error != null) {
            item {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            items(uiState.answers) { answer ->
                AnswerItem(answer = answer)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun AnswerItem(answer: Answer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Left Column: Score and Accepted
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Text(
                text = answer.score.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "votes", style = MaterialTheme.typography.labelSmall)
            if (answer.isAccepted) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Accepted",
                    tint = Color(0xFF45A341),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right Column: Body and Owner
        Column(modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
            ) {
            answer.body?.let {
                HtmlText(
                    html = it,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Answered ${formatDate(answer.creationDate)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OwnerCard(owner = answer.owner)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("MMM d, yyyy 'at' HH:mm", Locale.getDefault())
    return format.format(date)
}

