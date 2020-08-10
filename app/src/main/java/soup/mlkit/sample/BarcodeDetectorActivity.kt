package soup.mlkit.sample

import android.graphics.Bitmap
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.mlkit.sample.camera.CameraActivity
import timber.log.Timber

class BarcodeDetectorActivity : CameraActivity() {

    private var barcodeDetector: BarcodeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeDetector = BarcodeDetector {
//            val size = min(
//                it.boundingBox?.width() ?: 10,
//                it.boundingBox?.height() ?: 10
//            )
//            it.toBarcodeImage(size)
//            Toast.makeText(applicationContext, "rawValue=${it.rawValue}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDetected(bitmap: Bitmap, rotationDegrees: Int) {
        barcodeDetector?.detect(FirebaseVisionImage.fromBitmap(bitmap))
    }

    private class BarcodeDetector(
        private val onDetected: (List<FirebaseVisionBarcode>) -> Unit
    ) {

        private val detector = FirebaseVision.getInstance()
            .getVisionBarcodeDetector(
                FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
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
