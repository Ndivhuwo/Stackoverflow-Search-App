package za.co.ndivhuwo.stackoverflow_search_app.data.api

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class StackOverflowApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: StackOverflowApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val json = Json { ignoreUnknownKeys = true }
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(StackOverflowApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `searchQuestions returns correct response`() = runTest {
        val mockJsonResponse = """
            {
                "items": [
                    {
                        "question_id": 12345,
                        "title": "Kotlin Test Question",
                        "tags": ["kotlin"],
                        "owner": { "display_name": "Test User" },
                        "is_answered": true,
                        "view_count": 100,
                        "answer_count": 2,
                        "score": 10,
                        "creation_date": 1628604773,
                        "last_activity_date": 1772824126,
                        "link": "https://example.com"
                    }
                ],
                "has_more": false,
                "quota_max": 300,
                "quota_remaining": 299
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockJsonResponse).setResponseCode(200))

        val response = api.searchQuestions(title = "kotlin")

        assertEquals(1, response.items.size)
        assertEquals(12345L, response.items[0].questionId)
        assertEquals("Kotlin Test Question", response.items[0].title)
        assertEquals("Test User", response.items[0].owner.displayName)
    }

    @Test
    fun `getAnswers returns correct response`() = runTest {
        val mockJsonResponse = """
            {
                "items": [
                    {
                        "answer_id": 98765,
                        "question_id": 12345,
                        "body": "This is a test answer",
                        "owner": { "display_name": "Answerer" },
                        "is_accepted": true,
                        "score": 5,
                        "creation_date": 1628604773,
                        "last_activity_date": 1772824126
                    }
                ],
                "has_more": false,
                "quota_max": 300,
                "quota_remaining": 299
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockJsonResponse).setResponseCode(200))

        val response = api.getAnswers(questionIds = "12345")

        assertEquals(1, response.items.size)
        assertEquals(98765L, response.items[0].answerId)
        assertEquals("This is a test answer", response.items[0].body)
        assertEquals("Answerer", response.items[0].owner.displayName)
    }
}
