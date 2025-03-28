# Dokumentasi Proyek: Drafy - Android Note-Taking App

<img src="https://github.com/user-attachments/assets/5fa7fb79-c7ed-486b-8bfa-e009220daad0" width="200" height="auto" alt="Drafy Screenshot 1" />
<img src="https://github.com/user-attachments/assets/18e986d0-1ab1-4c8e-8879-a045b42c66ec" width="200" height="auto" alt="Drafy Screenshot 2" />
<img src="https://github.com/user-attachments/assets/949022cc-8bd7-434c-a345-0267b22decfa" width="200" height="auto" alt="Drafy Screenshot 3" />
<img src="https://github.com/user-attachments/assets/0bad6419-e782-45d1-ba6c-715888341db8" width="200" height="auto" alt="Drafy Screenshot 4" />


## 1. Judul dan Deskripsi Proyek
**Nama Proyek:** Drafy

**Deskripsi:** 
Drafy adalah aplikasi catatan modern yang dikembangkan dengan Jetpack Compose dan arsitektur MVVM. Aplikasi ini memungkinkan pengguna untuk membuat, mengedit, dan mengelola catatan dengan antarmuka yang intuitif. Fitur utama meliputi tema gelap/terang, pencarian konten Wikipedia, ekspor catatan ke PDF, dan fungsionalitas undo/redo. Drafy ditujukan untuk pengguna yang membutuhkan solusi pencatatan yang fleksibel namun tetap sederhana dengan tampilan UI yang modern.

## 2. Persyaratan Sistem (System Requirements)

### Perangkat Keras:
- Minimum 2 GB RAM
- Minimal ruang penyimpanan 50 MB

### Perangkat Lunak:
- Android Studio Arctic Fox (2021.3.1) atau lebih tinggi
- JDK 11 atau lebih tinggi
- SDK Android API Level 21 (Lollipop) atau lebih tinggi
- Kotlin 1.6.0 atau lebih tinggi
- Jetpack Compose 1.1.0 atau lebih tinggi

### Dependensi Utama:
- androidx.compose.ui:ui
- androidx.compose.material3:material3
- androidx.navigation:navigation-compose
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.room:room-runtime & room-ktx
- androidx.hilt:hilt-navigation-compose
- androidx.datastore:datastore-preferences
- retrofit2:retrofit & retrofit2:converter-moshi
- com.itextpdf:itextpdf (untuk ekspor PDF)

## 3. Instalasi dan Konfigurasi

### Langkah 1: Mengunduh dan Menginstal Dependensi
1. Pastikan Android Studio telah terpasang.
2. Di dalam proyek Android, buka file build.gradle (project level) dan pastikan memiliki repository google() dan mavenCentral().
3. Di dalam file build.gradle (module level), tambahkan dependensi berikut:

```gradle
// Hilt
implementation "com.google.dagger:hilt-android:$hilt_version"
kapt "com.google.dagger:hilt-android-compiler:$hilt_version"
implementation "androidx.hilt:hilt-navigation-compose:$hilt_navigation_version"

// Jetpack Compose
implementation "androidx.compose.ui:ui:$compose_version"
implementation "androidx.compose.material3:material3:$compose_material3_version"
implementation "androidx.activity:activity-compose:$activity_compose_version"
implementation "androidx.navigation:navigation-compose:$nav_version"
implementation "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"

// Room Database
implementation "androidx.room:room-runtime:$room_version"
implementation "androidx.room:room-ktx:$room_version"
kapt "androidx.room:room-compiler:$room_version"

// DataStore Preferences
implementation "androidx.datastore:datastore-preferences:$datastore_version"

// Retrofit for Wikipedia API
implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
implementation "com.squareup.retrofit2:converter-moshi:$retrofit_version"
implementation "com.squareup.okhttp3:logging-interceptor:$okhttp_version"

// PDF Generation
implementation "com.itextpdf:itextpdf:$itext_version"
```

### Langkah 2: Pengaturan File Konfigurasi
- File AndroidManifest.xml: Menambahkan izin yang diperlukan untuk ekspor PDF dan akses internet.
- Mengaktifkan Hilt di Application class:
```kotlin
@HiltAndroidApp
class DrafyApplication : Application()
```

### Langkah 3: Menyiapkan Lingkungan Pengembangan
1. Buka Android Studio.
2. Import proyek Drafy.
3. Sinkronisasi proyek dengan file Gradle.
4. Build dan jalankan aplikasi pada emulator atau perangkat fisik.

## 4. Struktur Direktori (Directory Structure)

```
com.example.drafy/
├── data/
│   ├── local/
│   │   ├── dao/            # Data Access Objects untuk Room
│   │   ├── entity/         # Model entitas database
│   │   └── DrafyDatabase.kt
│   ├── preferences/
│   │   └── ThemePreferences.kt
│   ├── remote/
│   │   ├── WikipediaApi.kt
│   │   ├── WikipediaRepository.kt
│   │   └── Models.kt
│   └── repository/
│       └── NoteRepository.kt
├── di/
│   ├── AppModule.kt        # Dependency Injection untuk komponen aplikasi
│   └── RepositoryModule.kt
├── ui/
│   ├── component/
│   │   ├── NoteCard.kt     # Komponen UI yang dapat digunakan kembali
│   │   └── WikipediaDialog.kt
│   ├── navigation/
│   │   └── Navigation.kt   # Konfigurasi navigasi aplikasi
│   ├── screen/
│   │   ├── home/           # Layar utama dengan daftar catatan
│   │   ├── note/           # Layar edit/tambah catatan
│   │   └── splash/         # Layar splash
│   └── theme/              # Pengaturan tema UI
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   ├── DateTimeConverter.kt # Utilitas konversi untuk database
│   └── PDFGenerator.kt      # Utilitas ekspor PDF
└── DrafyApplication.kt      # Kelas aplikasi untuk Hilt
```

## 5. Penjelasan Tentang Kode Sumber (Source Code Explanation)

### File Utama:

#### 1. MainActivity.kt
Titik masuk utama aplikasi yang mengatur tema dan menginisialisasi navigasi.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrafyTheme(themePreferences = themePreferences) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}
```

#### 2. HomeViewModel.kt
ViewModel untuk layar utama yang mengelola daftar catatan, preferensi tema, dan integrasi Wikipedia.

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val themePreferences: ThemePreferences,
    private val wikipediaRepository: WikipediaRepository
) : ViewModel() {
    // Flow untuk mode gelap
    val darkMode = themePreferences.isDarkMode.stateIn(...)
    
    // Flow untuk daftar catatan
    val notes = noteRepository.getAllNotes().stateIn(...)
    
    // Fungsi untuk toggle mode gelap
    fun toggleDarkMode() { ... }
    
    // Fungsi untuk pencarian Wikipedia
    fun searchWikipedia(query: String, onResult: (List<SearchResult>) -> Unit) { ... }
    
    // Fungsi untuk mendapatkan konten Wikipedia
    fun getWikipediaContent(pageId: Int, onResult: (String) -> Unit) { ... }
    
    // Fungsi untuk menghapus catatan
    fun deleteNotes(noteIds: List<Long>) { ... }
}
```

#### 3. NoteViewModel.kt
ViewModel untuk layar edit/tambah catatan yang mengelola status UI, riwayat perubahan (undo/redo), dan ekspor PDF.

```kotlin
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // ID catatan dari navigasi
    private val noteId: Long = savedStateHandle["noteId"] ?: -1L
    
    // Status UI
    private val _uiState = MutableStateFlow(NoteUiState(isLoading = true))
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()
    
    // Riwayat perubahan untuk undo/redo
    private val contentHistory = mutableListOf<String>()
    private var currentHistoryIndex = -1
    
    // Fungsi untuk load catatan
    private fun loadNote(id: Long) { ... }
    
    // Fungsi untuk mengubah judul
    fun onTitleChange(title: String) { ... }
    
    // Fungsi untuk mengubah konten
    fun onContentChange(content: String) { ... }
    
    // Fungsi untuk menyimpan catatan
    fun saveNote() { ... }
    
    // Fungsi untuk ekspor ke PDF
    fun exportToPdf() { ... }
    
    // Fungsi undo/redo
    fun undo() { ... }
    fun redo() { ... }
}
```

#### 4. NoteRepository.kt
Repository yang mengelola akses ke data catatan melalui Room Database.

```kotlin
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)
}
```

#### 5. WikipediaRepository.kt
Repository yang mengelola akses ke API Wikipedia.

```kotlin
class WikipediaRepository @Inject constructor(
    private val wikipediaApi: WikipediaApi
) {
    suspend fun searchTerm(searchTerm: String): List<SearchResult> { ... }
    suspend fun getPageContent(pageId: Int): String { ... }
}
```

### Penjelasan Logika Kode:

1. **Arsitektur MVVM**:
   - **Model**: Representasi data (Note, SearchResult, dll)
   - **View**: Komponen UI Composable (HomeScreen, NoteScreen, dll)
   - **ViewModel**: Logika bisnis dan manajemen status (HomeViewModel, NoteViewModel)

2. **Dependency Injection dengan Hilt**:
   - Modul AppModule.kt menyediakan dependency Room Database, Retrofit, dan ThemePreferences
   - Modul RepositoryModule.kt menyediakan NoteRepository dan WikipediaRepository

3. **Room Database**:
   - Menyimpan catatan pengguna dengan entitas Note
   - Menggunakan TypeConverter untuk menangani tanggal dan waktu

4. **Datastore Preferences**:
   - Menyimpan preferensi tema (gelap/terang)
   - Diimplementasikan di ThemePreferences.kt

5. **Retrofit untuk API Wikipedia**:
   - WikipediaApi.kt mendefinisikan endpoint untuk pencarian dan pengambilan konten
   - Diimplementasikan melalui WikipediaRepository.kt

## 6. Penggunaan API (Wikipedia)

API yang Digunakan:
- Endpoint Wikipedia API untuk pencarian dan mengambil konten artikel

```kotlin
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
```

Contoh Penggunaan dalam WikipediaRepository:
```kotlin
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
```

## 7. Contoh Penggunaan (Usage Examples)

### Menjalankan Aplikasi:
1. Clone repository ini ke mesin lokal.
2. Buka dengan Android Studio.
3. Jalankan aplikasi dengan memilih emulator atau perangkat fisik.

### Membuat Catatan Baru:
1. Dari layar utama, klik tombol Floating Action Button (ikon +).
2. Masukkan judul dan konten catatan.
3. Tekan tombol "Simpan" (ikon check) di pojok kanan atas.

### Mengedit Catatan:
1. Dari layar utama, klik pada catatan yang ingin diedit.
2. Edit judul atau konten catatan.
3. Tekan tombol "Simpan" untuk menyimpan perubahan.

### Menggunakan Fitur Wikipedia:
1. Dari layar utama, klik ikon artikel di pojok kanan atas.
2. Masukkan kata kunci pencarian.
3. Pilih hasil pencarian untuk melihat konten lengkap.

### Mengekspor Catatan ke PDF:
1. Buka catatan yang ingin diekspor.
2. Klik ikon PDF di menu atas.
3. Pilih aplikasi untuk membuka atau berbagi PDF.

### Contoh Kode Composable HomeScreen:

```kotlin
@Composable
fun HomeScreen(
    onNavigateToNote: (Long?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    
    // UI implementation with Scaffold, LazyVerticalGrid, etc.
    // ...
}
```

## 8. Pengujian dan Test

### Jenis Pengujian:
- **Unit Test**: Menggunakan JUnit untuk pengujian logika dalam ViewModel dan Repository.
- **UI Test**: Menggunakan ComposeTestRule untuk pengujian antarmuka pengguna.

### Menjalankan Pengujian:
1. Di Android Studio, pilih konfigurasi test untuk menjalankan unit test.
2. Gunakan "Run with Coverage" untuk melihat cakupan kode pengujian.

### Kasus Pengujian Utama:
- Pengujian CRUD operasi pada NoteRepository
- Pengujian konversi tanggal di DateTimeConverter
- Pengujian WikipediaRepository untuk request API
- Pengujian UI untuk navigasi dan interaksi dasar

## 9. Troubleshooting dan Pemecahan Masalah

### Masalah Umum:

1. **Masalah Database Room:**
   - **Gejala**: Aplikasi crash saat mencoba akses database
   - **Solusi**: Pastikan migrasi database diatur dengan benar, atau hapus dan instal ulang aplikasi

2. **Masalah API Wikipedia:**
   - **Gejala**: Tidak bisa mendapatkan hasil pencarian
   - **Solusi**: Periksa koneksi internet, atau API Wikipedia mungkin sedang down, coba lagi nanti

3. **Ekspor PDF Gagal:**
   - **Gejala**: PDF tidak terbentuk atau tidak bisa dibuka
   - **Solusi**: Pastikan izin penyimpanan diberikan dan ada aplikasi pembaca PDF di perangkat

### Tips Debugging:

- Gunakan Logcat untuk melihat detail error
- Periksa data yang disimpan di Room menggunakan Database Inspector di Android Studio
- Aktifkan HttpLoggingInterceptor untuk melihat komunikasi API

## 10. Penjelasan tentang Struktur Database

Drafy menggunakan Room Database dengan struktur berikut:

### Tabel Note:
- **id**: Long (Primary Key, Auto-increment)
- **title**: String
- **content**: String
- **lastEditTime**: LocalDateTime
- **creationTime**: LocalDateTime

### Entity Class:
```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val lastEditTime: LocalDateTime,
    val creationTime: LocalDateTime
)
```

### Data Access Object (DAO):
```kotlin
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY lastEditTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
```

## 11. Lisensi dan Hak Cipta (License and Copyright)

**Lisensi:**
Proyek ini dilisensikan di bawah MIT License - lihat file [LICENSE.md].

**Hak Cipta:**
© Muhammad Alif Qadri.

## 12. Daftar Pustaka dan Referensi

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [ViewModel Documentation](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Dependency Injection with Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Wiki API Documentation](https://www.mediawiki.org/wiki/API:Main_page)
- [iText PDF Library](https://itextpdf.com/en/products/itext-7/itext-7-core)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
- [Material Design 3](https://m3.material.io/)
