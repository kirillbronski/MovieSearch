package com.bronski.moviesearch.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bronski.moviesearch.data.model.Movie
import com.bronski.moviesearch.data.repisitory.MovieRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> get() = _movies

    init {
        getNowPlayingMovies()
    }

    fun searchMovies(query: String) {
        compositeDisposable.add(
            repository.searchMovies(query)
                .cachedIn(viewModelScope)
                .subscribe {
                    _movies.value = it
                }
        )
    }

    private fun getNowPlayingMovies() {
        compositeDisposable.add(
            repository.getNowPlayingMovies()
                .cachedIn(viewModelScope)
                .subscribe {
                    _movies.value = it
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}