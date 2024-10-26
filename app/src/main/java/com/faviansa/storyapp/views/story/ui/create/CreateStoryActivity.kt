package com.faviansa.storyapp.views.story.ui.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.faviansa.storyapp.R
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.ActivityCreateStoryBinding
import com.faviansa.storyapp.utils.createCustomTempFile
import com.faviansa.storyapp.utils.displayToast
import com.faviansa.storyapp.utils.getRotatedBitmap
import com.faviansa.storyapp.utils.reduceFileImage
import com.faviansa.storyapp.utils.uriToFile
import com.faviansa.storyapp.views.story.StoryActivity
import com.faviansa.storyapp.views.story.ui.StoryViewModelFactory
import com.faviansa.storyapp.views.story.ui.StoryViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var preferences: StoryAppPreferences
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(this)
    }

    private val CAMERA_PERMISSION_CODE = 10
    private val CAMERA_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var cameraMode: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var viewfinderContainer: ConstraintLayout
    private lateinit var cameraPreview: PreviewView
    private lateinit var shutterButton: ImageButton
    private lateinit var switchCameraButton: ImageButton
    private lateinit var selectFromGalleryButton: Button

    private lateinit var storyEditorContainer: ConstraintLayout
    private lateinit var selectedImage: ImageView
    private lateinit var storyDescription: EditText
    private lateinit var publishButton: ImageButton

    private lateinit var loadingIndicator: ProgressBar

    private var selectedImageUri: Uri? = null
    private var isFrontFacing: Boolean = false
    private lateinit var imageCaptureUseCase: ImageCapture

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = StoryAppPreferences.getInstance(dataStore)
        initializeViews()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (storyEditorContainer.visibility == View.VISIBLE) {
            storyEditorContainer.visibility = View.GONE
            viewfinderContainer.visibility = View.VISIBLE
            selectedImageUri = null
            initializeCamera()
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializeViews() {
        with(binding) {
            viewfinderContainer = cameraxContainer
            cameraPreview = previewView
            switchCameraButton = buttonFlipCamera
            shutterButton = buttonCapture
            selectFromGalleryButton = buttonGallery

            storyEditorContainer = addStoryContainer
            selectedImage = ivImagePreview
            storyDescription = edAddDescription
            publishButton = buttonAdd

            loadingIndicator = progressBar
        }

        checkAndRequestPermissions()
        setupButtonListeners()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupButtonListeners() {
        switchCameraButton.setOnClickListener { toggleCamera() }
        shutterButton.setOnClickListener { captureImage() }
        selectFromGalleryButton.setOnClickListener { launchGallery() }
        publishButton.setOnClickListener { publishStory() }
    }

    private fun checkAndRequestPermissions() {
        if (hasRequiredPermissions()) {
            initializeCamera()
        } else {
            requestCameraPermissions()
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERA_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                baseContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            CAMERA_PERMISSIONS,
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && hasRequiredPermissions()) {
            initializeCamera()
        }
    }

    private fun initializeCamera() {
        if (viewfinderContainer.visibility != View.VISIBLE) return

        applyCameraScale()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            setupCameraProvider(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }

    private fun applyCameraScale() {
        cameraPreview.scaleX = if (cameraMode == CameraSelector.DEFAULT_FRONT_CAMERA) -1f else 1f
    }

    private fun setupCameraProvider(cameraProvider: ProcessCameraProvider) {
        imageCaptureUseCase = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraMode,
                preview,
                imageCaptureUseCase
            )
        } catch (exc: Exception) {
            displayToast(this, getString(R.string.camera_start_failed))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun publishStory() {
        try {
            if (selectedImageUri == null) {
                displayToast(this, getString(R.string.please_choose_a_picture_first))
                return
            }

            val description = storyDescription.text.toString()
            if (description.isEmpty()) {
                displayToast(this, getString(R.string.please_add_description))
                return
            }

            loadingIndicator.visibility = View.VISIBLE

            val processedImage = uriToFile(selectedImageUri!!, this).reduceFileImage()
            val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
            val imageBody = RequestBody.create("image/*".toMediaTypeOrNull(), processedImage)
            val imagePart =
                MultipartBody.Part.createFormData("photo", processedImage.name, imageBody)

            uploadToServer(descriptionBody, imagePart)
        } catch (e: Exception) {
            loadingIndicator.visibility = View.GONE
            displayToast(this, "Error: ${e.message}")
        }
    }

    private fun uploadToServer(description: RequestBody, image: MultipartBody.Part) {
        storyViewModel.createNewStory(description, image)

        storyViewModel.stories.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingIndicator.visibility = ProgressBar.VISIBLE
                }

                is Result.Success -> {
                    loadingIndicator.visibility = ProgressBar.GONE
                    displayToast(this, getString(R.string.story_uploaded_successfully))
                    onUploadSuccess()
                }

                is Result.Error -> {
                    loadingIndicator.visibility = ProgressBar.GONE
                    displayToast(this, getString(R.string.error, result.error))
                }
            }
        }
    }

    private fun onUploadSuccess() {
        val intent = Intent(this, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun toggleCamera() {
        isFrontFacing = !isFrontFacing
        cameraMode = if (isFrontFacing) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        initializeCamera()
    }

    private fun captureImage() {
        if (!::imageCaptureUseCase.isInitialized) {
            return displayToast(this, getString(R.string.camera_is_not_ready))
        }

        val photoFile = createCustomTempFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCaptureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    processAndDisplayCapturedImage(output, photoFile)
                }

                override fun onError(exc: ImageCaptureException) {
                    displayToast(
                        this@CreateStoryActivity,
                        getString(R.string.capture_failed, exc.message)
                    )
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun processAndDisplayCapturedImage(
        output: ImageCapture.OutputFileResults,
        photoFile: File,
    ) {
        try {
            selectedImageUri = output.savedUri ?: Uri.fromFile(photoFile)
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            val rotatedBitmap = bitmap.getRotatedBitmap(photoFile)

            val processedFile = createCustomTempFile(application)
            FileOutputStream(processedFile).use { outputStream ->
                rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            runOnUiThread {
                selectedImage.setImageURI(Uri.fromFile(processedFile))
                viewfinderContainer.visibility = View.GONE
                storyEditorContainer.visibility = View.VISIBLE
            }

            selectedImageUri = Uri.fromFile(processedFile)
        } catch (e: Exception) {
            runOnUiThread {
                displayToast(this, "Error: ${e.message}")
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    selectedImageUri = uri
                    runOnUiThread {
                        selectedImage.setImageURI(uri)
                        viewfinderContainer.visibility = View.GONE
                        storyEditorContainer.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    displayToast(this, "Error loading image: ${e.message}")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        initializeCamera()
    }
}