package com.bronski.moviesearch.data.api

import com.bronski.moviesearch.data.model.Movie

data class MovieResponse(
    val results: List<Movie>,
)