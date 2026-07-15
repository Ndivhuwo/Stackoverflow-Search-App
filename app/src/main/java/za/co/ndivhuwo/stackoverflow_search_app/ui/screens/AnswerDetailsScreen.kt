package za.co.ndivhuwo.stackoverflow_search_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import za.co.ndivhuwo.stackoverflow_search_app.R
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Answer
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.ErrorView
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.HtmlText
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.SimpleHtmlText
import za.co.ndivhuwo.stackoverflow_search_app.ui.components.TagChip
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.AnswerUiState
import za.co.ndivhuwo.stackoverflow_search_app.viewmodel.AnswerViewModel
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
            onRetry = viewModel::retry,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AnswerDetailsContent(
    question: Question,
    uiState: AnswerUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soBlue = Color(0xFF0074CC)
    val soGray = Color(0xFF6A737C)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Question Section
        item {
            Column {

                SimpleHtmlText(
                    html = question.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = soBlue,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetadataItem(label = "Asked", value = formatDate(question.creationDate))
                    MetadataItem(label = "Viewed", value = formatCount(question.viewCount))
                    MetadataItem(label = "Score", value = question.score.toString())
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                question.body?.let {
                    HtmlText(
                        html = it,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (question.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        question.tags.forEach { tag ->
                            TagChip(tag = tag)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                OwnerBlock(owner = question.owner, date = question.creationDate, label = "asked")
            }
        }

        // Answers Header
        item {
            Column {
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${uiState.answers.size} Answers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Answers List
        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.error != null) {
            item {
                ErrorView(
                    message = uiState.error!!,
                    onRetry = onRetry
                )
            }
        } else {
            items(uiState.answers) { answer ->
                AnswerItem(answer = answer)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun AnswerItem(answer: Answer) {
    val soGray = Color(0xFF6A737C)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = soGray, modifier = Modifier.size(32.dp))
            Text(
                text = answer.score.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = soGray
            )
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = soGray, modifier = Modifier.size(32.dp))
            
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

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            answer.body?.let {
                HtmlText(
                    html = it,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            OwnerBlock(owner = answer.owner, date = answer.creationDate, label = "answered")
        }
    }
}

@Composable
fun MetadataItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6A737C)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF232629),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OwnerBlock(owner: Owner, date: Long, label: String) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1ECF4)),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "$label ${formatDate(date)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6A737C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                ) {
                    if (!owner.profileImage.isNullOrEmpty()) {
                        AsyncImage(
                            model = owner.profileImage,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = owner.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF0074CC)
                    )
                    owner.reputation?.let {
                        Text(
                            text = formatCount(it.toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A737C)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return format.format(date)
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "%.1fm".format(count / 1000000.0)
        count >= 1000 -> "%.1fk".format(count / 1000.0)
        else -> count.toString()
    }
}
