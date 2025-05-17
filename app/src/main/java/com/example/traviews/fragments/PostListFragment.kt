package com.example.traviews.fragments

import PostAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traviews.R
import com.example.traviews.model.Post
import kotlinx.coroutines.launch

class PostListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val viewModel: FeedViewModel by viewModels()
    private var posts: List<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.feedUiState.collect { state ->
                when (state) {
                    is FeedUiState.Loading -> {
                        println("Carregando Posts")
                    }

                    is FeedUiState.Success -> {
                        posts = state.posts

                        postAdapter = PostAdapter(posts)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.adapter = postAdapter
                    }

                    is FeedUiState.Error -> {
                        println("Falha ao carregar o cep")
                    }
                }
            }
        }
    }
}
