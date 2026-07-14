package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QuestionItem(
    question: Question,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val stackOverflowBlue = Color(0xFF0074CC)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 1. Answered Icon
            Box(modifier = Modifier.size(24.dp)) {
                if (question.isAnswered) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Answered",
                        tint = Color(0xFF45A341), // Success green
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Not Answered",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Main Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = question.title,
                    color = stackOverflowBlue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                question.body?.let {
                    HtmlText(
                        html = it,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Asked ${formatDate(question.creationDate)} by ${question.owner.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = stackOverflowBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 3. Stats
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.width(50.dp)
            ) {
                StatItem(count = question.answerCount, label = "answers")
                StatItem(count = question.score, label = "votes")
                StatItem(count = question.viewCount, label = "views")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 4. Details Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Text(
            text = formatCount(count),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "%.1fm".format(count / 1000000.0)
        count >= 1000 -> "%.1fk".format(count / 1000.0)
        else -> count.toString()
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return format.format(date)
}

@Preview(showBackground = true)
@Composable
fun QuestionItemPreview() {
    StackoverflowSearchAppTheme {
        QuestionItem(
            question = Question(
                questionId = 1,
                title = "How to see local variables values when debugging Kotlin Coroutines in IDEA?",
                body = "When the debugger stops on breakpoint somewhere in Kotlin Coroutines code, I usually suffer from the inability to access local variables values (they are out of scope and inaccessible due to the nature of Coroutines implementation).",
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
    }
}
