package soup.mlkit.sample

import android.graphics.Bitmap
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import soup.mlkit.sample.camera.CameraActivity
import timber.log.Timber

class RealtimeLabelDetectorActivity : CameraActivity() {

    private var labelDetector: LabelDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        labelDetector = LabelDetector {
            viewModel.onTextResult(it.map { imageLabel ->
                imageLabel.text + ", confidence=" + imageLabel.confidence
            })
        }
    }

    override fun onDetected(bitmap: Bitmap, rotationDegrees: Int) {
        labelDetector?.detect(FirebaseVisionImage.fromBitmap(bitmap))
    }

    private class LabelDetector(
        private val onDetected: (List<FirebaseVisionImageLabel>) -> Unit
    ) {

        private val detector = FirebaseVision.getInstance().getOnDeviceImageLabeler(
            FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.75f)
                .build()
        )

        fun detect(image: FirebaseVisionImage) {
            detector
                .processImage(image)
                .addOnSuccessListener {
                    onDetected(it.orEmpty())
                }
                .addOnFailureListener {
                    Timber.w(it)
                }
        }
    }
}
