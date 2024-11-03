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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.FragmentRegisterBinding
import com.faviansa.storyapp.utils.displayToast
import com.faviansa.storyapp.views.custom.EmailEditText
import com.faviansa.storyapp.views.custom.MyButton
import com.faviansa.storyapp.views.custom.MyEditText
import com.faviansa.storyapp.views.custom.PasswordEditText
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var nameEditText: MyEditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var registerButton: MyButton
    private lateinit var preferences: StoryAppPreferences
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory.getInstance(requireActivity(), preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = StoryAppPreferences.getInstance(requireActivity().dataStore)

        setupView()
        setupAction()
        setupAnimation()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        nameEditText = binding.edRegisterName
        emailEditText = binding.edRegisterEmail
        passwordEditText = binding.edRegisterPassword
        registerButton = binding.registerButton

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                checkEditTextErrors()
            }

        })

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
        binding.btnToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.registerButton.setOnClickListener {
            viewModel.register(name, email, password)
        }
    }

    private fun checkEditTextErrors() {
        name = nameEditText.text.toString()
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()

        registerButton.isEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                && nameEditText.error == null && emailEditText.error == null && passwordEditText.error == null
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        displayToast(requireActivity(), result.data.message.toString())
                        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        displayToast(requireActivity(), result.error)
                    }
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

        val titleTv = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(150)
        val nameTv = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(150)
        val nameEditText =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(150)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(150)
        val passwordTv =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(150)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(150)
        val registerButton =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(150)
        val tvRegistered =
            ObjectAnimator.ofFloat(binding.tvRegistered, View.ALPHA, 1f).setDuration(150)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}