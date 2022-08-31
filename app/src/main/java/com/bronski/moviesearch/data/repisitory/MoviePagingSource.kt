package com.bronski.moviesearch.data.repisitory

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.bronski.moviesearch.data.api.ApiService
import com.bronski.moviesearch.data.api.MovieResponse
import com.bronski.moviesearch.data.model.Movie
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MoviePagingSource(
    private val apiService: ApiService,
    private val query: String?,
) : RxPagingSource<Int, Movie>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val page = params.key ?: 1
        return if (query != null) {
            apiService.searchMovies(query, page)
                .subscribeOn(Schedulers.io())
                .map { toLoadResult(it, page) }
                .onErrorReturn { LoadResult.Error(it) }
        } else {
            apiService.getNowPlayingMovies(page)
                .subscribeOn(Schedulers.io())
                .map { toLoadResult(it, page) }
                .onErrorReturn { LoadResult.Error(it) }
        }
    }

    private fun toLoadResult(
        television: MovieResponse,
        page: Int
    ): LoadResult<Int, Movie> {
        return LoadResult.Page(
            data = television.results,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (television.results.isEmpty()) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}