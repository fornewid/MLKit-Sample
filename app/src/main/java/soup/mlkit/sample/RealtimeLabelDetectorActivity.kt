package soup.mlkit.sample

import android.graphics.Bitmap
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.mlkit.sample.camera.CameraActivity

class RealtimeLabelDetectorActivity : CameraActivity() {

    override fun onDetected(bitmap: Bitmap, rotationDegrees: Int) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
    }
}