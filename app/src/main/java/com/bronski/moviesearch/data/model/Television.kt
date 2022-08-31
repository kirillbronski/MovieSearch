package com.bronski.moviesearch.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Television(
    val id: Int,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
    val name: String,
    @SerializedName("first_air_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: String
) : Parcelable