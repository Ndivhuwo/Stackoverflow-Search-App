package za.co.ndivhuwo.stackoverflow_search_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import za.co.ndivhuwo.stackoverflow_search_app.ui.screens.HomeScreen
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackoverflowSearchAppTheme {
                HomeScreen(
                    onQuestionClick = { question ->
                        val intent = Intent(this, AnswerDetailsActivity::class.java).apply {
                            putExtra(AnswerDetailsActivity.EXTRA_QUESTION_ID, question.questionId)
                            putExtra(AnswerDetailsActivity.EXTRA_QUESTION_JSON, Json.encodeToString(question))
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
