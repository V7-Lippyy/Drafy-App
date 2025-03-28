// WikipediaRepository.kt
package com.example.drafy.data.remote

import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val wikipediaApi: WikipediaApi
) {
    suspend fun searchTerm(searchTerm: String): List<SearchResult> {
        return wikipediaApi.searchTerm(searchTerm = searchTerm).query.search
    }

    suspend fun getPageContent(pageId: Int): String {
        val response = wikipediaApi.getPageExtract(pageId = pageId.toString())
        return response.query.pages.values.firstOrNull()?.extract ?: "Tidak ada informasi tersedia"
    }
}