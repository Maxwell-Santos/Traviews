package com.example.traviews.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traviews.databinding.FragmentUserProfileBinding
import com.example.traviews.ui.screens.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserProfileFragment: Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val viewModel: UserProfileViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.userProfileUiState.collect { state ->
                when (state) {
                    is UserProfileUiState.Loading -> {
                        println("Carregando...")
                    }

                    is UserProfileUiState.Success -> {
                        binding.tvUserProileEmail.text = state.userProfile.email
                        binding.tvUserProfileName.text = state.userProfile.name
                    }

                    is UserProfileUiState.Error -> {
                        println("Falha ao carregar o usuário")
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}