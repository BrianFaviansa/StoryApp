package com.faviansa.storyapp.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faviansa.storyapp.databinding.FragmentLoginBinding
import com.faviansa.storyapp.views.custom.EmailEditText
import com.faviansa.storyapp.views.custom.MyButton
import com.faviansa.storyapp.views.custom.PasswordEditText


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginButton: MyButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAnimation()
        setupAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword
        loginButton = binding.loginButton

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                checkEditTextErrors()
            }

        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                checkEditTextErrors()
            }

        })
    }

    private fun setupAction() {
        binding.btnToRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
    }

    private fun checkEditTextErrors() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                && emailEditText.error == null && passwordEditText.error == null
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