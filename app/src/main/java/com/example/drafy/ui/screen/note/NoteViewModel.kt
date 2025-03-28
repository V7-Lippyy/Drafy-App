// NoteViewModel.kt
package com.example.drafy.ui.screen.note

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drafy.data.local.entity.Note
import com.example.drafy.data.repository.NoteRepository
import com.example.drafy.util.PDFGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class NoteUiState(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val lastEditTime: LocalDateTime = LocalDateTime.now(),
    val creationTime: LocalDateTime = LocalDateTime.now(),
    val characterCount: Int = 0,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val noteId: Long = savedStateHandle["noteId"] ?: -1L

    private val _uiState = MutableStateFlow(NoteUiState(isLoading = true))
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    // For undo/redo functionality
    private val contentHistory = mutableListOf<String>()
    private var currentHistoryIndex = -1

    init {
        if (noteId != -1L) {
            loadNote(noteId)
        } else {
            _uiState.value = NoteUiState(
                title = "",
                content = "",
                lastEditTime = LocalDateTime.now(),
                creationTime = LocalDateTime.now(),
                isLoading = false
            )
            addToHistory("")
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val note = noteRepository.getNoteById(id)
            if (note != null) {
                _uiState.value = _uiState.value.copy(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    lastEditTime = note.lastEditTime,
                    creationTime = note.creationTime,
                    characterCount = note.content.length,
                    isLoading = false,
                    isSaved = true
                )
                addToHistory(note.content)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            isSaved = false
        )
    }

    fun onContentChange(content: String) {
        _uiState.value = _uiState.value.copy(
            content = content,
            characterCount = content.length,
            isSaved = false
        )
        addToHistory(content)
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val now = LocalDateTime.now()

            val note = Note(
                id = currentState.id,
                title = currentState.title.ifEmpty { "Tanpa Judul" },
                content = currentState.content,
                lastEditTime = now,
                creationTime = if (currentState.id == 0L) now else currentState.creationTime
            )

            if (currentState.id == 0L) {
                val newId = noteRepository.insertNote(note)
                _uiState.value = currentState.copy(
                    id = newId,
                    lastEditTime = now,
                    isSaved = true
                )
            } else {
                noteRepository.updateNote(note)
                _uiState.value = currentState.copy(
                    lastEditTime = now,
                    isSaved = true
                )
            }
        }
    }

    fun exportToPdf() {
        val currentState = _uiState.value

        // Create a single note list for the PDF generator
        val noteList = listOf(
            Note(
                id = currentState.id,
                title = currentState.title.ifEmpty { "Tanpa Judul" },
                content = currentState.content,
                lastEditTime = currentState.lastEditTime,
                creationTime = currentState.creationTime
            )
        )

        // Use the PDFGenerator utility
        PDFGenerator.createNotePDF(context, noteList)
    }

    // Undo functionality
    private fun addToHistory(content: String) {
        // Remove any forward history if we're adding after an undo
        if (currentHistoryIndex < contentHistory.size - 1) {
            contentHistory.subList(currentHistoryIndex + 1, contentHistory.size).clear()
        }

        contentHistory.add(content)
        currentHistoryIndex = contentHistory.size - 1

        // Limit history size
        if (contentHistory.size > 50) {
            contentHistory.removeAt(0)
            currentHistoryIndex--
        }
    }

    fun undo() {
        if (currentHistoryIndex > 0) {
            currentHistoryIndex--
            _uiState.value = _uiState.value.copy(
                content = contentHistory[currentHistoryIndex],
                characterCount = contentHistory[currentHistoryIndex].length,
                isSaved = false
            )
        }
    }

    fun redo() {
        if (currentHistoryIndex < contentHistory.size - 1) {
            currentHistoryIndex++
            _uiState.value = _uiState.value.copy(
                content = contentHistory[currentHistoryIndex],
                characterCount = contentHistory[currentHistoryIndex].length,
                isSaved = false
            )
        }
    }
}