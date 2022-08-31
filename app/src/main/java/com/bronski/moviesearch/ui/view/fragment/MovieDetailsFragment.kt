package com.bronski.moviesearch.ui.view.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import com.bronski.moviesearch.R
import com.bronski.moviesearch.data.api.ApiService
import com.bronski.moviesearch.databinding.FragmentMovieDetailsBinding

@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<MovieDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMovieDetailsBinding.bind(view)

        binding.apply {
            val movie = args.movie
            Glide.with(this@MovieDetailsFragment)
                .load("${ApiService.IMAGE_BASE_URL}${movie.posterPath}")
                .error(R.drawable.ic_image_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewMovieDescription.isVisible = true
                        textViewMovieTitle.isVisible = true
                        return false
                    }
                })
                .into(imageViewMoviePoster)

            textViewMovieDescription.text = movie.overview
            textViewMovieTitle.text = movie.originalTitle
        }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}