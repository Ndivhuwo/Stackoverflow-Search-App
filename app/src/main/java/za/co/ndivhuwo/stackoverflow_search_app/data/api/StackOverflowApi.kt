package za.co.ndivhuwo.stackoverflow_search_app.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import za.co.ndivhuwo.stackoverflow_search_app.data.models.StackOverflowResponse

interface StackOverflowApi {
    @GET("search/advanced")
    suspend fun searchQuestions(
        @Query("title") title: String,
        @Query("pagesize") pageSize: Int = 3,
        @Query("site") site: String = "stackoverflow",
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("filter") filter: String = "withbody"
    ): StackOverflowResponse

    companion object {
        const val BASE_URL = "https://api.stackexchange.com/2.3/"
    }
}
