package soup.mlkit.sample

import com.google.mlkit.vision.common.InputImage
import soup.mlkit.sample.picker.PickerActivity

class DummyActivity : PickerActivity() {

    override fun onDetected(input: InputImage) {
    }
}
