package com.memealbum.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memealbum.app.data.model.MemeItem
import com.memealbum.app.data.repository.MemeRepository
import kotlinx.coroutines.launch

enum class SortType { HOT, NEW, TOP }
enum class LoadState { IDLE, LOADING, LOADING_MORE, SUCCESS, ERROR }

class MemeViewModel : ViewModel() {

    private val repository = MemeRepository()

    private val _memes = MutableLiveData<List<MemeItem>>(emptyList())
    val memes: LiveData<List<MemeItem>> = _memes

    private val _loadState = MutableLiveData(LoadState.IDLE)
    val loadState: LiveData<LoadState> = _loadState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var afterToken: String? = null
    var currentSort = SortType.HOT
        private set

    private val _selectedMemes = MutableLiveData<Set<String>>(emptySet())
    val selectedMemes: LiveData<Set<String>> = _selectedMemes

    init {
        loadMemes()
    }

    fun loadMemes(sort: SortType = currentSort) {
        currentSort = sort
        afterToken = null
        _loadState.value = LoadState.LOADING
        viewModelScope.launch {
            try {
                val (memes, after) = when (sort) {
                    SortType.HOT -> repository.getHotMemes()
                    SortType.NEW -> repository.getNewMemes()
                    SortType.TOP -> repository.getTopMemes()
                }
                afterToken = after
                _memes.value = memes
                _loadState.value = LoadState.SUCCESS
            } catch (e: Exception) {
                _errorMessage.value = "Không tải được meme: ${e.message}"
                _loadState.value = LoadState.ERROR
            }
        }
    }

    fun loadMore() {
        if (_loadState.value == LoadState.LOADING_MORE) return
        val token = afterToken ?: return

        _loadState.value = LoadState.LOADING_MORE
        viewModelScope.launch {
            try {
                val (newMemes, after) = when (currentSort) {
                    SortType.HOT -> repository.getHotMemes(after = token)
                    SortType.NEW -> repository.getNewMemes(after = token)
                    SortType.TOP -> repository.getTopMemes(after = token)
                }
                afterToken = after
                val current = _memes.value.orEmpty().toMutableList()
                current.addAll(newMemes)
                _memes.value = current
                _loadState.value = LoadState.SUCCESS
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tải thêm: ${e.message}"
                _loadState.value = LoadState.ERROR
            }
        }
    }

    fun refresh() = loadMemes(currentSort)

    fun toggleSelection(id: String) {
        val current = _selectedMemes.value.orEmpty().toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedMemes.value = current
    }

    fun clearSelection() {
        _selectedMemes.value = emptySet()
    }

    fun deleteSelected() {
        val selected = _selectedMemes.value.orEmpty()
        _memes.value = _memes.value.orEmpty().filter { it.id !in selected }
        clearSelection()
    }

    fun hasMore() = afterToken != null
}
