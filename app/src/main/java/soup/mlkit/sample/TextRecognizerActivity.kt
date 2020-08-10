package soup.mlkit.sample

import android.net.Uri
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionText.*
import soup.mlkit.sample.picker.PickerActivity
import soup.mlkit.sample.result.DrawObject
import timber.log.Timber

class TextRecognizerActivity : PickerActivity() {

    private var textRecognizer: TextRecognizer? = null

    private fun Element.toDrawObject(): DrawObject? {
        return boundingBox?.let { boundingBox ->
            DrawObject.TextObject(text, boundingBox)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textRecognizer = TextRecognizer {
            Timber.d("text=${it.text}")
            viewModel.onDrawResult(it.textBlocks
                .flatMap(TextBlock::getLines)
                .flatMap(Line::getElements)
                .mapNotNull { element -> element.toDrawObject() })
        }
    }

    override fun onDetected(uri: Uri) {
        textRecognizer?.detect(FirebaseVisionImage.fromFilePath(this, uri))
    }

    private class TextRecognizer(
        private val onRecognized: (FirebaseVisionText) -> Unit
    ) {

        private val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

        fun detect(image: FirebaseVisionImage) {
            recognizer
                .processImage(image)
                .addOnSuccessListener {
                    onRecognized(it)
                }
                .addOnFailureListener {
                    Timber.w(it)
                }
        }
    }
}
