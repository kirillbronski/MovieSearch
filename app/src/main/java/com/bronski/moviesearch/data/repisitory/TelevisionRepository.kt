package com.bronski.moviesearch.data.repisitory

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava3.flowable
import com.bronski.moviesearch.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelevisionRepository @Inject constructor(
    private val apiService: ApiService,
) {

    fun getTvPopular() =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                maxSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TelevisionPagingSource(apiService, null) }
        ).flowable

    fun searchTv(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                maxSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TelevisionPagingSource(apiService, query) }
        ).flowable

}