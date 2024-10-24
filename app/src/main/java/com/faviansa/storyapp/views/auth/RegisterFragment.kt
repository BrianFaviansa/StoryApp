package com.faviansa.storyapp.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faviansa.storyapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        setupAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAction() {
        binding.btnToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
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

        val titleTv = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(150)
        val nameTv = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(150)
        val nameEditText = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(150)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailEditText = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(150)
        val passwordTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(150)
        val passwordEditText = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(150)
        val registerButton = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(150)
        val tvRegistered = ObjectAnimator.ofFloat(binding.tvRegistered, View.ALPHA, 1f).setDuration(150)
        val btnToLogin = ObjectAnimator.ofFloat(binding.btnToLogin, View.ALPHA, 1f).setDuration(150)

        val together2 = AnimatorSet().apply {
            playTogether(tvRegistered, btnToLogin)
        }

        AnimatorSet().apply {
            playSequentially(
                titleTv,
                nameTv,
                nameEditText,
                emailTv,
                emailEditText,
                passwordTv,
                passwordEditText,
                registerButton,
                together2
            )
            startDelay = 200
            start()
        }
    }
}