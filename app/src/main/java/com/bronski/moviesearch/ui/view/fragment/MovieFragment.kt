package com.bronski.moviesearch.ui.view.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import com.bronski.moviesearch.R
import com.bronski.moviesearch.data.model.Movie
import com.bronski.moviesearch.databinding.FragmentMovieBinding
import com.bronski.moviesearch.ui.adapter.MovieAdapter
import com.bronski.moviesearch.ui.adapter.MovieLoadStateAdapter
import com.bronski.moviesearch.ui.viewmodel.MovieViewModel
import kotlinx.coroutines.*

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnItemClickListener {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MovieViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMovieBinding.bind(view)

        val adapter = MovieAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { adapter.retry() },
                footer = MovieLoadStateAdapter { adapter.retry() }
            )
            buttonTryAgain.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.movies.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonTryAgain.isVisible = loadState.source.refresh is LoadState.Error
                textViewFailed.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewNotFound.isVisible = true
                } else {
                    textViewNotFound.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.menuSearch)
        val searchView = searchItem.actionView as SearchView
        var job: Job? = null
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    newText?.let {
                        delay(500)
                        if (newText.isNotEmpty()) {
                            binding.recyclerView.scrollToPosition(0)
                            viewModel.searchMovies(newText)
                        }
                    }
                }
                return true
            }
        })
    }

    override fun onItemClick(movie: Movie) {
        findNavController().navigate(
            MovieFragmentDirections.actionMovieFragmentToDetailsFragment(
                movie
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}