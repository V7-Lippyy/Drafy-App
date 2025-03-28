// HomeViewModel.kt
package com.example.drafy.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drafy.data.local.entity.Note
import com.example.drafy.data.preferences.ThemePreferences
import com.example.drafy.data.remote.SearchResult
import com.example.drafy.data.remote.WikipediaRepository
import com.example.drafy.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val themePreferences: ThemePreferences,
    private val wikipediaRepository: WikipediaRepository
) : ViewModel() {

    val darkMode = themePreferences.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val notes = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferences.toggleDarkMode()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun searchWikipedia(query: String, onResult: (List<SearchResult>) -> Unit) {
        viewModelScope.launch {
            try {
                val results = wikipediaRepository.searchTerm(query)
                onResult(results)
            } catch (e: Exception) {
                // Handle error
                onResult(emptyList())
            }
        }
    }

    fun getWikipediaContent(pageId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val content = wikipediaRepository.getPageContent(pageId)
                onResult(content)
            } catch (e: Exception) {
                // Handle error
                onResult("Gagal mengambil konten: ${e.message}")
            }
        }
    }

    fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            for (id in noteIds) {
                noteRepository.deleteNoteById(id)
            }
        }
    }
}