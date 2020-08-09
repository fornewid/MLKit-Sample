package soup.mlkit.sample

import android.net.Uri
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.mlkit.sample.picker.PickerActivity

class LabelDetectorActivity : PickerActivity() {

    override fun onDetected(uri: Uri) {
        val image = FirebaseVisionImage.fromFilePath(this, uri)
    }
}