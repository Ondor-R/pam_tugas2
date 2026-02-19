package com.example.newsfeedsim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import newsfeedsim.composeapp.generated.resources.Res
import newsfeedsim.composeapp.generated.resources.compose_multiplatform

data class News(val id: Int, val title: String, val category: String)

// flow yg mensimulasikan berita baru per 2 detik
fun newsFlow(): Flow<News> = flow {
    var counter = 1
    val categories = listOf("Teknologi", "Olahraga", "Politik")
    while (true) {
        delay(2000L) //detiknya
        emit(News(counter, "Berita Terkini $counter", categories.random()))
        counter++
    }
}


//fungsi utk simulasi mengambil detail berita, digunakan secara async di line 66, 77, 88
suspend fun fetchNewsDetail(newsId: Int): String {
    delay(1000L)
    return "(ID:$newsId) Detail lengkap untuk berita..."
}

//StateFlow untuk menyimpan jumlah berita yang sudah dibaca
val countTeknologi = MutableStateFlow(0)
val countOlahraga = MutableStateFlow(0)
val countPolitik = MutableStateFlow(0)

@Composable
@Preview
fun App() {

    var newsTeknologi by remember { mutableStateOf("Menunggu berita teknologi...") }
    var newsOlahraga by remember { mutableStateOf("Menunggu berita olahraga...") }
    var newsPolitik by remember { mutableStateOf("Menunggu berita politik...") }

    val cTeknologi by countTeknologi.collectAsState()
    val cOlahraga by countOlahraga.collectAsState()
    val cPolitik by countPolitik.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            newsFlow()
                .filter { it.category == "Teknologi" } //Filter berita berdasarkan kategori tertentu
                .map { "[${it.category}] ${it.title}" } //Transform data menjadi format yang ditampilkan
                .collect { formattedNews ->
                    val detail = async { fetchNewsDetail(countTeknologi.value + 1) }.await()
                    newsTeknologi = "$formattedNews \n$detail"
                    countTeknologi.value++
                }
        }

        launch {
            newsFlow()
                .filter { it.category == "Olahraga" } //Filter berita berdasarkan kategori tertentu
                .map { "[${it.category}] ${it.title}" } //Transform data menjadi format yang ditampilkan
                .collect { formattedNews ->
                    val detail = async { fetchNewsDetail(countOlahraga.value + 1) }.await()
                    newsOlahraga = "$formattedNews \n$detail"
                    countOlahraga.value++
                }
        }

        launch {
            newsFlow()
                .filter { it.category == "Politik" } //Filter berita berdasarkan kategori tertentu
                .map { "[${it.category}] ${it.title}" } //Transform data menjadi format yang ditampilkan
                .collect { formattedNews ->
                    val detail = async { fetchNewsDetail(countPolitik.value + 1) }.await()
                    newsPolitik = "$formattedNews \n$detail"
                    countPolitik.value++
                }
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Total Teknologi dibaca: $cTeknologi", style = MaterialTheme.typography.titleMedium)
            Text(text = newsTeknologi)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Total Olahraga dibaca: $cOlahraga", style = MaterialTheme.typography.titleMedium)
            Text(text = newsOlahraga)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Total Politik dibaca: $cPolitik", style = MaterialTheme.typography.titleMedium)
            Text(text = newsPolitik)
        }
    }
}