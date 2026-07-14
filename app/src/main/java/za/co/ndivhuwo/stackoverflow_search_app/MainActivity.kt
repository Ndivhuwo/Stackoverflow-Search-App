package za.co.ndivhuwo.stackoverflow_search_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Question
import za.co.ndivhuwo.stackoverflow_search_app.ui.screens.AnswerDetailsScreen
import za.co.ndivhuwo.stackoverflow_search_app.ui.screens.HomeScreen
import za.co.ndivhuwo.stackoverflow_search_app.ui.theme.StackoverflowSearchAppTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackoverflowSearchAppTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController, 
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreen(
                            onQuestionClick = { question ->
                                val questionJson = Json.encodeToString(question)
                                val encodedJson = URLEncoder.encode(questionJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("details/${question.questionId}?questionJson=$encodedJson")
                            }
                        )
                    }
                    composable(
                        route = "details/{questionId}?questionJson={questionJson}",
                        arguments = listOf(
                            navArgument("questionId") { type = NavType.LongType },
                            navArgument("questionJson") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val encodedJson = backStackEntry.arguments?.getString("questionJson")
                        val question = remember(encodedJson) {
                            encodedJson?.let { 
                                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Json.decodeFromString<Question>(decodedJson) 
                            }
                        }
                        
                        if (question != null) {
                            AnswerDetailsScreen(
                                question = question,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
