package com.bronski.moviesearch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bronski.moviesearch.R
import com.bronski.moviesearch.data.api.ApiService
import com.bronski.moviesearch.data.model.Movie
import com.bronski.moviesearch.databinding.ItemRowBinding

class MovieAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        MovieViewHolder(
            ItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class MovieViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(movie: Movie) =
            with(binding) {
                Glide.with(itemView)
                    .load("${ApiService.IMAGE_BASE_URL}${movie.posterPath}")
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_image_error)
                    .into(imageViewPoster)
                textViewTitle.text = movie.originalTitle
                textViewOverview.text = movie.overview
                textViewReleaseDate.text = movie.releaseDate
                textViewVoteAverage.text = movie.voteAverage
            }

    }

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }

    object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }

}