package com.feldman.tiktek.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.feldman.tiktek.LocalRepository
import com.feldman.tiktek.R
import com.feldman.tiktek.data.network.Book
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BooksScreen(onOpenBook: (String, String) -> Unit, modifier: Modifier = Modifier) {
    val repo = LocalRepository.current
    val scope = rememberCoroutineScope()

    val subjectIds = listOf(
        "ST2016" to "מתמטיקה",
        "ST2021" to "אנגלית",
        "ST2012" to "היסטוריה",
        "ST2005" to "תנ\"ך",
        "ST2009" to "לשון והבעה עברית",
        "ST2013" to "אזרחות",
        "ST2019" to "פיזיקה",
        "ST2032" to "ביולוגיה",
        "ST2036" to "כימיה",
        "ST2017" to "מדעי המחשב",
        "ST2039" to "חינוך גופני",
        "ST2014" to "גיאוגרפיה",
        "ST2010" to "סוציולוגיה ומדעי החברה",
        "ST2037" to "מדעי כדור הארץ והסביבה",
        "ST2018" to "מדעים וטכנולוגיה",
        "ST2041" to "ביו טכנולוגיה",
        "ST2042" to "חשמל, אלקטרוניקה",
        "ST2046" to "תעשייה וניהול",
        "ST2043" to "ניהול עסקי",
        "ST2047" to "טכנולוגיות מידע",
        "ST2045" to "טכנולוגיות תקשורת",
        "ST2015" to "תקשורת וקולנוע",
        "ST2033" to "פילוסופיה",
        "ST2048" to "פסיכולוגיה",
        "ST2029" to "אמנות חזותית",
        "ST2027" to "מוזיקה",
        "ST2030" to "מחול",
        "ST2028" to "תיאטרון",
        "ST2031" to "מכונות",
        "ST2044" to "מכונאות רכב ותחבורה",
        "ST2020" to "מדעי החקלאות",
        "ST2034" to "לימודי ארץ ישראל",
        "ST2007" to "מורשת ותרבות ישראל",
        "ST2008" to "מחשבת ישראל",
        "ST2006" to "תורה שבע\"פ ותלמוד",
        "ST2051" to "מורשת דרוזית",
        "ST2040" to "נושאים מיוחדים במדע",
        "ST2049" to "לימודי אסלאם",
        "ST2050" to "לימודי נצרות",
        "ST2022" to "ערבית",
        "ST2023" to "צרפתית",
        "ST2025" to "ספרדית",
        "ST2024" to "רוסית",
        "ST2053" to "סינית",
        "ST2026" to "אמהרית"
    )

    var selectedSubject by remember { mutableStateOf(subjectIds.first()) }
    var query by remember { mutableStateOf(TextFieldValue("")) }

    var cachedBooks by rememberSaveable { mutableStateOf<Map<String, List<Book>>>(emptyMap()) }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedSubject) {
        val subjectId = selectedSubject.first
        val cached = cachedBooks[subjectId]
        if (cached != null) {
            books = cached
            return@LaunchedEffect
        }

        loading = true
        error = null
        try {
            val fetched = repo.fetchBooks(subjectId)
            books = fetched
            cachedBooks = cachedBooks + (subjectId to fetched)
        } catch (t: Throwable) {
            error = t.message
        } finally {
            loading = false
        }
    }

    val favorites by repo.favoritesFlow.collectAsState(initial = emptySet())



    val filteredBooks = remember(books, query, favorites) {
        val q = query.text.trim().lowercase()
        val base = if (q.isEmpty()) books else books.filter { it.title.lowercase().contains(q) }
        base.sortedWith(compareByDescending<Book> { favorites.contains(it.id) }.thenBy { it.title })
    }

    val listState = rememberLazyListState()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Tiktek",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = selectedSubject.second,
                    shape = if (expanded) {
                        RoundedCornerShape(
                            topStart = 32.dp,
                            topEnd = 32.dp,
                            bottomStart = 6.dp,
                            bottomEnd = 6.dp
                        )
                    } else {
                        RoundedCornerShape(50)
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subject") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .height(64.dp)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 6.dp,
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                ) {
                    subjectIds.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedSubject = id to name
                                expanded = false
                            }
                        )
                    }
                }

            }

            Spacer(Modifier.height(8.dp))
            val focusManager = LocalFocusManager.current

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search books") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        scope.launch {
                            loading = true
                            error = null
                            try {
                                val results = repo.searchBooks(selectedSubject.first, query.text)
                                books = results
                                cachedBooks = cachedBooks + (selectedSubject.first to results)
                            } catch (t: Throwable) {
                                error = t.message
                            } finally {
                                loading = false
                            }
                        }
                    }
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            if (loading) {
                LinearWavyProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    stroke = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    trackStroke = Stroke(width = 12f),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    waveSpeed = 100.dp,
                    wavelength = 50.dp

                )
            }

            error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBooks) { b ->
                    BookRow(
                        book = b,
                        isFavorite = favorites.contains(b.id),
                        onToggleFavorite = { scope.launch { repo.toggleFavorite(b.id) } },
                        onClick = { onOpenBook(b.id, b.title) }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showScrollToTop) {
            FloatingActionButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun BookRow(
    book: Book,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val repo = LocalRepository.current
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = repo.bookCoverUrlFor(book),
            contentDescription = book.title,
            modifier = Modifier
                .size(56.dp),
            error = painterResource(id = R.drawable.ic_book)
        )


        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium)
            val subtitle = listOfNotNull(book.bt1, book.bt2, book.bt3).joinToString(" · ")
            if (subtitle.isNotBlank())
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                painter = painterResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border),
                contentDescription = "Favorite"
            )
        }
    }
}