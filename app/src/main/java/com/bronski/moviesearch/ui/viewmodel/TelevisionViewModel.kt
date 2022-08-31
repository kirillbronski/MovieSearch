package com.bronski.moviesearch.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bronski.moviesearch.data.model.Television
import com.bronski.moviesearch.data.repisitory.TelevisionRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class TelevisionViewModel @Inject constructor(
    private val repository: TelevisionRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _tv = MutableLiveData<PagingData<Television>>()
    val tv: LiveData<PagingData<Television>> get() = _tv

    init {
        getTelevisionPopular()
    }

    private fun getTelevisionPopular() {
        compositeDisposable.add(
            repository.getTvPopular()
                .cachedIn(viewModelScope)
                .subscribe {
                    _tv.value = it
                }
        )
    }

    fun searchTelevision(query: String) {
        compositeDisposable.add(
            repository.searchTv(query)
                .cachedIn(viewModelScope)
                .subscribe {
                    _tv.value = it
                }
        )
    }

}