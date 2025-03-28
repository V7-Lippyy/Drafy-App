package com.example.drafy.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drafy.data.local.entity.Note
import com.example.drafy.data.remote.SearchResult
import com.example.drafy.ui.component.WikipediaDialog
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNote: (Long?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    // Tema modern dengan warna yang lebih hidup
    val backgroundColor = if (darkMode) Color(0xFF121212) else Color(0xFFF8F9FA)
    val cardColor = if (darkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (darkMode) Color(0xFFF5F5F5) else Color(0xFF202124)
    val secondaryTextColor = if (darkMode) Color(0xFFAAAAAA) else Color(0xFF5F6368)
    val accentColor = Color(0xFFFFA000) // Warna aksen oranye keemasan
    val searchBarColor = if (darkMode) Color(0xFF2D2D2D) else Color(0xFFEEEEEE)

    // Status untuk semua fitur
    var showWikipediaDialog by remember { mutableStateOf(false) }
    var wikipediaSearchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var selectedWikipediaContent by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var isLoadingContent by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var deleteMode by remember { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<Long>() }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Filter catatan berdasarkan query pencarian
    val filteredNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notes.filter { note ->
            note.title.contains(searchQuery, ignoreCase = true) ||
                    note.content.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            if (deleteMode) "Pilih Catatan" else "Catatan",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    },
                    actions = {
                        if (deleteMode) {
                            // Tombol konfirmasi hapus dalam mode hapus
                            IconButton(
                                onClick = {
                                    if (selectedNotes.isNotEmpty()) {
                                        showDeleteConfirmation = true
                                    }
                                },
                                enabled = selectedNotes.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Catatan Terpilih",
                                    tint = if (selectedNotes.isNotEmpty()) Color.Red else secondaryTextColor
                                )
                            }

                            // Tombol batal dengan desain modern
                            TextButton(
                                onClick = {
                                    deleteMode = false
                                    selectedNotes.clear()
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = textColor
                                )
                            ) {
                                Text("Batal", fontWeight = FontWeight.Medium)
                            }
                        } else {
                            // Tombol-tombol aksi dengan desain modern
                            IconButton(onClick = { deleteMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Catatan",
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = { showSearchBar = !showSearchBar }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Cari Catatan",
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = { showWikipediaDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Article,
                                    contentDescription = "Cari di Wikipedia",
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                Icon(
                                    imageVector = if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Ubah Mode Tema",
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )

                // Search bar dengan animasi halus
                AnimatedVisibility(
                    visible = showSearchBar && !deleteMode,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(32.dp)),
                        placeholder = {
                            Text(
                                "Cari catatan",
                                color = secondaryTextColor,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = secondaryTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(32.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = searchBarColor,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor,
                            cursorColor = accentColor,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        floatingActionButton = {
            if (!deleteMode) {
                FloatingActionButton(
                    onClick = { onNavigateToNote(null) },
                    containerColor = accentColor,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Tambah Catatan",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "Belum ada catatan" else "Tidak ada hasil pencarian",
                    style = MaterialTheme.typography.bodyLarge,
                    color = secondaryTextColor
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotes) { note ->
                    val isSelected = selectedNotes.contains(note.id)

                    // Card modern dengan shadow dan elevasi yang lebih halus
                    ElevatedCard(
                        onClick = {
                            if (deleteMode) {
                                if (isSelected) {
                                    selectedNotes.remove(note.id)
                                } else {
                                    selectedNotes.add(note.id)
                                }
                            } else {
                                onNavigateToNote(note.id)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = cardColor,
                            contentColor = textColor
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Box {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = note.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = textColor,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (deleteMode) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                            contentDescription = if (isSelected) "Dipilih" else "Tidak Dipilih",
                                            tint = if (isSelected) Color(0xFF4CAF50) else secondaryTextColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = note.content,
                                    fontSize = 14.sp,
                                    color = secondaryTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Styling untuk timestamp
                                Text(
                                    text = note.lastEditTime.format(
                                        DateTimeFormatter.ofPattern("EEEE, dd MMMM HH:mm").withLocale(Locale("id", "ID"))
                                    ),
                                    fontSize = 12.sp,
                                    color = secondaryTextColor.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialog konfirmasi hapus dengan desain modern
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = {
                    Text(
                        "Konfirmasi Hapus",
                        fontWeight = FontWeight.Bold,
                        color = if (darkMode) Color.White else Color.Black
                    )
                },
                text = {
                    Text(
                        text = "Apakah Anda yakin ingin menghapus ${selectedNotes.size} catatan yang dipilih?",
                        color = if (darkMode) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteNotes(selectedNotes.toList())
                            selectedNotes.clear()
                            deleteMode = false
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text("Batal", fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = if (darkMode) Color(0xFF2D2D2D) else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Wikipedia dialog
        WikipediaDialog(
            isShowing = showWikipediaDialog,
            onDismiss = {
                showWikipediaDialog = false
                selectedWikipediaContent = null
            },
            onSearch = { query ->
                isSearching = true
                viewModel.searchWikipedia(query) { results ->
                    wikipediaSearchResults = results
                    isSearching = false
                }
            },
            onGetContent = { pageId ->
                isLoadingContent = true
                viewModel.getWikipediaContent(pageId) { content ->
                    selectedWikipediaContent = content
                    isLoadingContent = false
                }
            },
            searchResults = wikipediaSearchResults,
            selectedContent = selectedWikipediaContent,
            isLoading = isLoadingContent,
            isSearching = isSearching,
            darkMode = darkMode
        )
    }
}