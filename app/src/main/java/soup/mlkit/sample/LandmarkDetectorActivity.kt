package soup.mlkit.sample

import android.net.Uri
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.mlkit.sample.picker.PickerActivity
import timber.log.Timber

class LandmarkDetectorActivity : PickerActivity() {

    private var landmarkDetector: LandmarkDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        landmarkDetector = LandmarkDetector {
            viewModel.onTextResult(it.map { landmark ->
                landmark.landmark + ", confidence=" + landmark.confidence
            })
        }
    }

    override fun onDetected(uri: Uri) {
        landmarkDetector?.detect(FirebaseVisionImage.fromFilePath(this, uri))
    }

    private class LandmarkDetector(
        private val onDetected: (List<FirebaseVisionCloudLandmark>) -> Unit
    ) {

        private val detector = FirebaseVision.getInstance()
            .getVisionCloudLandmarkDetector(
                FirebaseVisionCloudDetectorOptions.Builder()
                    .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                    .setMaxResults(15)
                    .build()
            )

        fun detect(image: FirebaseVisionImage) {
            detector
                .detectInImage(image)
                .addOnSuccessListener {
                    onDetected(it.orEmpty())
                }
                .addOnFailureListener {
                    Timber.w(it)
                }
        }
    }
}
