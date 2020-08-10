package soup.mlkit.sample

import android.graphics.Bitmap
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.mlkit.sample.camera.CameraActivity
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeDetectorActivity : CameraActivity() {

    private var barcodeDetector: BarcodeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeDetector = BarcodeDetector {
            Timber.d(it.mapNotNull { barcode -> barcode.rawValue }.joinToString(separator = "\n"))
            viewModel.onDrawBarcode(it)
            viewModel.onTextResult(it.mapNotNull { barcode -> barcode.rawValue })
        }
    }

    override fun onDetected(bitmap: Bitmap) {
        barcodeDetector?.detect(FirebaseVisionImage.fromBitmap(bitmap))
    }

    private class BarcodeDetector(
        private val onDetected: (List<FirebaseVisionBarcode>) -> Unit
    ) {

        private val isInDetecting = AtomicBoolean(false)

        private val detector = FirebaseVision.getInstance()
            .getVisionBarcodeDetector(
                FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                    .build()
            )

        fun isInDetecting(): Boolean {
            return isInDetecting.get()
        }

        fun detect(image: FirebaseVisionImage) {
            if (isInDetecting.compareAndSet(false, true)) {
                detector
                    .detectInImage(image)
                    .addOnSuccessListener {
                        onDetected(it.orEmpty())
                    }
                    .addOnFailureListener {
                        Timber.w(it)
                    }
                    .addOnCompleteListener {
                        isInDetecting.set(false)
                    }
            }
        }
    }
}
