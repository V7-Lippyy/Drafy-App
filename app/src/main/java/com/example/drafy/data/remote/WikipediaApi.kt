// WikipediaApi.kt
package com.example.drafy.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApi {
    @GET("api.php")
    suspend fun searchTerm(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") searchTerm: String,
        @Query("srlimit") limit: Int = 5
    ): WikipediaResponse

    @GET("api.php")
    suspend fun getPageExtract(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "extracts",
        @Query("exintro") exintro: Boolean = true,
        @Query("explaintext") explaintext: Boolean = true,
        @Query("pageids") pageId: String
    ): WikipediaExtractResponse
}

@JsonClass(generateAdapter = true)
data class WikipediaResponse(
    val query: QueryResult
)

@JsonClass(generateAdapter = true)
data class QueryResult(
    val search: List<SearchResult>
)

@JsonClass(generateAdapter = true)
data class SearchResult(
    val pageid: Int,
    val title: String,
    val snippet: String
)

@JsonClass(generateAdapter = true)
data class WikipediaExtractResponse(
    val query: Pages
)

@JsonClass(generateAdapter = true)
data class Pages(
    val pages: Map<String, PageContent>
)

@JsonClass(generateAdapter = true)
data class PageContent(
    val pageid: Int,
    val title: String,
    val extract: String
)