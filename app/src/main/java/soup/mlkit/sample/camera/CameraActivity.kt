package soup.mlkit.sample.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import soup.mlkit.sample.databinding.CameraBinding
import soup.mlkit.sample.result.MLKitResultViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class CameraActivity : AppCompatActivity() {

    private lateinit var binding: CameraBinding
    protected val viewModel: MLKitResultViewModel by viewModels()

    private val cameraExecutor: ExecutorService = Executors.newCachedThreadPool()
    private val scopedExecutor: ScopedExecutor = ScopedExecutor(cameraExecutor)

    abstract fun onDetected(bitmap: Bitmap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isPermissionsGranted(CAMERA_PERMISSION)) {
            binding.bindCameraUseCases()
        } else {
            requestPermissions(CAMERA_PERMISSION, REQUEST_CODE_CAMERA)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scopedExecutor.shutdown()
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun CameraBinding.bindCameraUseCases() = previewView.post {
        val context = root.context
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.display.rotation)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                scopedExecutor, ImageAnalysis.Analyzer { proxy ->
                    proxy.use {
                        val bitmap = proxy.image?.use {
                            val rotationDegrees = when (proxy.imageInfo.rotationDegrees) {
                                0 -> FirebaseVisionImageMetadata.ROTATION_0
                                90 -> FirebaseVisionImageMetadata.ROTATION_90
                                180 -> FirebaseVisionImageMetadata.ROTATION_180
                                270 -> FirebaseVisionImageMetadata.ROTATION_270
                                else -> FirebaseVisionImageMetadata.ROTATION_0
                            }
                            FirebaseVisionImage.fromMediaImage(it, rotationDegrees).bitmap
                        }
                        if (bitmap != null) {
                            viewModel.onImageSizeChanged(bitmap.width, bitmap.height)
                            onDetected(bitmap)
                        }
                    }
                }
            )

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this@CameraActivity,
                cameraSelector,
                preview,
                imageAnalysis
            )

            // Use the camera object to link our preview use case with the view
            preview.setSurfaceProvider(previewView.createSurfaceProvider())

        }, ContextCompat.getMainExecutor(context))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                if (isPermissionsGranted(CAMERA_PERMISSION)) {
                    binding.bindCameraUseCases()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun Context.isPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {

        private const val REQUEST_CODE_CAMERA = 10

        private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }
}
