package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val soBlue = Color(0xFF0074CC)
    val soGray = Color(0xFF6A737C)
    val soAcceptedGreen = Color(0xFF45A341)
    
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // 1. Stats Column
                Column(
                    modifier = Modifier.width(60.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    StatItem(
                        count = question.score,
                        label = "votes",
                        color = if (question.score > 0) Color(0xFF232629) else soGray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val hasAnswers = question.answerCount > 0
                    val isAnswered = question.isAnswered
                    
                    val answerBg = if (isAnswered) soAcceptedGreen else Color.Transparent
                    val answerContent = if (isAnswered) Color.White else if (hasAnswers) soAcceptedGreen else soGray
                    val answerBorder = if (hasAnswers && !isAnswered) BorderStroke(1.dp, soAcceptedGreen) else null
                    
                    Surface(
                        color = answerBg,
                        contentColor = answerContent,
                        border = answerBorder,
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        StatItem(
                            count = question.answerCount,
                            label = "answers",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "${formatCount(question.viewCount)} views",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFAC3931),
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 2. Main Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SimpleHtmlText(
                        html = question.title,
                        color = soBlue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2,
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    question.body?.let {
                        HtmlText(
                            html = it,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF3B4045)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Tags
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(question.tags) { tag ->
                                TagChip(tag = tag)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // User & Date
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = question.owner.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = soBlue
                            )
                            Text(
                                text = " asked ${formatDate(question.creationDate)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = soGray
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = formatCount(count),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 10.sp
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
