// NoteScreen.kt
package com.example.drafy.ui.screen.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drafy.data.preferences.ThemePreferences
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()

    // Theme colors based on dark mode state
    val backgroundColor = if (darkMode) Color.Black else Color.White
    val textColor = if (darkMode) Color.White else Color.Black
    val secondaryTextColor = if (darkMode) Color(0xFF9E9E9E) else Color(0xFF757575)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { /* Empty title */ },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.undo() }) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = textColor
                        )
                    }

                    IconButton(onClick = { viewModel.redo() }) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = textColor
                        )
                    }

                    IconButton(onClick = { viewModel.exportToPdf() }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF",
                            tint = textColor
                        )
                    }

                    IconButton(onClick = { viewModel.saveNote() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Simpan",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    actionIconContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = textColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp)
            ) {
                // Title field - borderless, large text
                TextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    placeholder = {
                        Text(
                            "Judul",
                            color = secondaryTextColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    textStyle = TextStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 0.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        disabledContainerColor = backgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = textColor
                    )
                )

                // Meta information with proper padding
                Text(
                    text = "${uiState.lastEditTime.format(DateTimeFormatter.ofPattern("dd MMMM HH:mm"))} | ${uiState.characterCount} karakter",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 0.dp, bottom = 16.dp)
                )

                // Content field - borderless
                TextField(
                    value = uiState.content,
                    onValueChange = { viewModel.onContentChange(it) },
                    placeholder = {
                        Text(
                            "Mulai mengetik",
                            color = secondaryTextColor
                        )
                    },
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        disabledContainerColor = backgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = textColor
                    )
                )
            }
        }
    }
}