package com.feldman.tiktek.data.repo


import android.content.Context
import com.feldman.tiktek.data.network.*
import com.feldman.tiktek.data.store.FavoritesStore
import kotlinx.coroutines.flow.Flow


class TiktekRepository(
    private val api: TiktekApi,
    appContext: Context
) {
    private val favoritesStore = FavoritesStore(appContext)


    suspend fun fetchBooks(subjectId: String): List<Book> {
        val resp = api.getBooks(GetBooksRequest(subjectID = subjectId))
        if (!resp.d.success) error("GetBooks failed: ${'$'}{resp.d.messageCode}")
        return resp.d.resultData ?: emptyList()
    }


    suspend fun fetchSolutions(bookId: String, page: Int, question: Int): List<Solution> {
        val body = GetSolutionsExRequest(
            bookID = bookId,
            page = page.toString(),
            question = question.toString()
        )
        println(body)
        println(body)
        println(body)
        println(body)
        println(body)
        println(body)
        val resp = api.getSolutionsEx(body)
        if (!resp.d.success) error("GetSolutionsEx failed: ${'$'}{resp.d.messageCode}")
        return resp.d.resultData ?: emptyList()
    }


    fun imageUrlFor(sol: Solution): String =
        "https://tiktek.com/il/tt-resources/solution-images/${sol.prefix}_${sol.bookId}/${sol.image}"

    fun bookCoverUrlFor(book: Book): String {
        return "https://tiktek.com/il/tt-resources-unmanaged/books-covers/${book.image ?: ""}"
    }

    // Favorites
    val favoritesFlow: Flow<Set<String>> = favoritesStore.favoritesFlow
    suspend fun toggleFavorite(bookId: String) = favoritesStore.toggle(bookId)
    suspend fun isFavorite(bookId: String) = favoritesStore.isFavorite(bookId)

    suspend fun searchBooks(subjectId: String, query: String): List<Book> {
        val all = fetchBooks(subjectId)
        return all.filter { it.title.contains(query, ignoreCase = true) }
    }
}