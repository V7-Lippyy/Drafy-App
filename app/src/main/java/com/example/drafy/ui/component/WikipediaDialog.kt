// WikipediaDialog.kt
package com.example.drafy.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drafy.data.remote.SearchResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikipediaDialog(
    isShowing: Boolean,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit,
    onGetContent: (Int) -> Unit,
    searchResults: List<SearchResult>,
    selectedContent: String?,
    isLoading: Boolean,
    isSearching: Boolean,
    darkMode: Boolean
) {
    if (!isShowing) return

    // Warna yang disesuaikan berdasarkan mode
    val backgroundColor = if (darkMode) Color.Black else Color.White
    val cardColor = if (darkMode) Color(0xFF212121) else Color(0xFFF5F5F5)
    val textColor = if (darkMode) Color.White else Color.Black
    val secondaryTextColor = if (darkMode) Color(0xFFAAAAAA) else Color.Gray
    val searchBarColor = if (darkMode) Color(0xFF303030) else Color(0xFFEEEEEE)
    val buttonColor = if (darkMode) Color(0xFFFFA726) else Color(0xFFFFA726) // Orange for both themes
    val buttonTextColor = Color.White // White text for button in both themes

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(isShowing) {
        if (!isShowing) {
            searchQuery = ""
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Cari di Wikipedia",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Kata Kunci", color = secondaryTextColor) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = searchBarColor,
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor,
                    cursorColor = textColor,
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = secondaryTextColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSearch(searchQuery) },
                modifier = Modifier.fillMaxWidth(),
                enabled = searchQuery.isNotBlank() && !isSearching,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor,
                    disabledContainerColor = buttonColor.copy(alpha = 0.5f),
                    disabledContentColor = buttonTextColor.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Cari")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSearching || isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = if (darkMode) Color(0xFFFFA726) else Color(0xFFFFA726)
                    )
                }
            } else if (selectedContent != null) {
                Text(
                    text = selectedContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = buttonColor
                    )
                ) {
                    Text("Tutup")
                }
            } else if (searchResults.isNotEmpty()) {
                LazyColumn {
                    items(searchResults) { result ->
                        WikipediaResultItem(
                            result = result,
                            onClick = { onGetContent(result.pageid) },
                            darkMode = darkMode,
                            cardColor = cardColor,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor
                        )
                    }
                }
            } else if (searchQuery.isNotBlank() && !isSearching) {
                Text(
                    text = "Tidak ada hasil ditemukan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikipediaResultItem(
    result: SearchResult,
    onClick: () -> Unit,
    darkMode: Boolean,
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = result.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result.snippet,
                fontSize = 14.sp,
                color = secondaryTextColor
            )
        }
    }
}