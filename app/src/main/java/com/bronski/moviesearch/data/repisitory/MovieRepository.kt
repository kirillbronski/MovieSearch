package com.bronski.moviesearch.data.repisitory

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava3.flowable
import com.bronski.moviesearch.data.api.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class MovieRepository @Inject constructor(
    private val apiService: ApiService,
) {

    fun getNowPlayingMovies() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, null) }
        ).flowable

    fun searchMovies(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, query) }
        ).flowable

}