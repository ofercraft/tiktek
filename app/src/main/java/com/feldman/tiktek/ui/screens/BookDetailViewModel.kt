package com.feldman.tiktek.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feldman.tiktek.data.network.Solution
import com.feldman.tiktek.data.repo.TiktekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookId: String,
    private val repo: TiktekRepository
) : ViewModel() {

    var page = MutableStateFlow("")
    var question = MutableStateFlow("")
    private val _solutions = MutableStateFlow<List<Solution>>(emptyList())
    val solutions: StateFlow<List<Solution>> = _solutions

    var loading = MutableStateFlow(false)
    var error = MutableStateFlow<String?>(null)

    fun fetchSolutions() {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val p = page.value.toIntOrNull() ?: 0
                val q = question.value.toIntOrNull() ?: 0
                _solutions.value = repo.fetchSolutions(bookId, p, q)
            } catch (t: Throwable) {
                error.value = t.message
            } finally {
                loading.value = false
            }
        }
    }
}