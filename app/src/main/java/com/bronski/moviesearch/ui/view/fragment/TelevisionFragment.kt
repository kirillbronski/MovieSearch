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
import com.bronski.moviesearch.data.model.Television
import com.bronski.moviesearch.databinding.FragmentTelevisionBinding
import com.bronski.moviesearch.ui.adapter.MovieLoadStateAdapter
import com.bronski.moviesearch.ui.adapter.TelevisionAdapter
import com.bronski.moviesearch.ui.viewmodel.TelevisionViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TelevisionFragment : Fragment(R.layout.fragment_television),
    TelevisionAdapter.OnItemClickListener {

    private var _binding: FragmentTelevisionBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TelevisionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTelevisionBinding.bind(view)

        val adapter = TelevisionAdapter(this)

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

        viewModel.tv.observe(viewLifecycleOwner) {
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
                            viewModel.searchTelevision(newText)
                        }
                    }
                }
                return true
            }
        })
    }

    override fun onItemClick(television: Television) {
        findNavController().navigate(
            TelevisionFragmentDirections.actionTelevisionFragmentToTelevisionDetailsFragment(
                television
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}