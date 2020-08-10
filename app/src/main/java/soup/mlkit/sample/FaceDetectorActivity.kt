package soup.mlkit.sample

import android.graphics.Bitmap
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import soup.mlkit.sample.camera.CameraActivity
import soup.mlkit.sample.result.DrawObject
import timber.log.Timber

class FaceDetectorActivity : CameraActivity() {

    private var faceDetector: FaceDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        faceDetector = FaceDetector {
//            it.map { DrawObject.PointObject() }
//            viewModel.onDrawResult(listOf())
//            val size = min(
//                it.boundingBox?.width() ?: 10,
//                it.boundingBox?.height() ?: 10
//            )
//            it.toBarcodeImage(size)
//            Toast.makeText(applicationContext, "rawValue=${it.rawValue}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDetected(bitmap: Bitmap, rotationDegrees: Int) {
        faceDetector?.detect(FirebaseVisionImage.fromBitmap(bitmap))
    }

    private class FaceDetector(
        private val onDetected: (List<FirebaseVisionFace>) -> Unit
    ) {

        private val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(
                FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
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
