package com.faviansa.storyapp.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.FragmentLoginBinding
import com.faviansa.storyapp.views.custom.EmailEditText
import com.faviansa.storyapp.views.custom.MyButton
import com.faviansa.storyapp.views.custom.PasswordEditText
import com.faviansa.storyapp.views.story.StoryActivity


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginButton: MyButton
    private lateinit var btnToRegister: Button
    private lateinit var preferences: StoryAppPreferences
    private lateinit var email: String
    private lateinit var password: String
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory.getInstance(requireActivity(), preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = StoryAppPreferences.getInstance(requireActivity().dataStore)

        setupView()
        setupAnimation()
        setupAction()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword
        loginButton = binding.loginButton
        btnToRegister = binding.btnToRegister

        emailEditText.addTextChangedListener {
            checkEditTextErrors()
        }

        passwordEditText.addTextChangedListener {
            checkEditTextErrors()
        }
    }

    private fun setupAction() {
        btnToRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        loginButton.setOnClickListener {
            viewModel.login(email, password)
        }
    }

    private fun checkEditTextErrors() {
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()

        if (password.isEmpty()) {
            passwordEditText.error = null
        }

        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                && emailEditText.error == null && passwordEditText.error == null
    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), StoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.imageView, View.SCALE_X, 1f, 1.1f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(binding.imageView, View.SCALE_Y, 1f, 1.1f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val alpha = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 0.8f, 1f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            start()
        }

        val titleTv =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(150)
        val emailTv =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(150)
        val passwordTv =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(150)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(150)
        val loginButton =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(150)
        val tvNotRegistered =
            ObjectAnimator.ofFloat(binding.tvNotRegistered, View.ALPHA, 1f).setDuration(150)
        val btnToRegister =
            ObjectAnimator.ofFloat(binding.btnToRegister, View.ALPHA, 1f).setDuration(150)

        val together2 = AnimatorSet().apply {
            playTogether(tvNotRegistered, btnToRegister)
        }

        AnimatorSet().apply {
            playSequentially(
                titleTv,
                emailTv,
                emailEditText,
                passwordTv,
                passwordEditText,
                loginButton,
                together2
            )
            startDelay = 200
            start()
        }
    }
}