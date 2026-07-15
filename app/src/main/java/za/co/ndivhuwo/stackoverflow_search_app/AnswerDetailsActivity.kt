package za.co.ndivhuwo.stackoverflow_search_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.screens.AnswerDetailsScreen
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme

@AndroidEntryPoint
class AnswerDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val questionJson = intent.getStringExtra(EXTRA_QUESTION_JSON)
        val question = questionJson?.let { Json.decodeFromString<Question>(it) }
        
        if (question == null) {
            finish()
            return
        }
        
        setContent {
            StackoverflowSearchAppTheme {
                AnswerDetailsScreen(
                    question = question,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_QUESTION_ID = "questionId"
        const val EXTRA_QUESTION_JSON = "questionJson"
    }
}
