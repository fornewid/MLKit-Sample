package soup.mlkit.sample.result

import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.text.FirebaseVisionText

class MLKitResultViewModel : ViewModel() {

    private val _imageSize = MutableLiveData<Size>()
    val imageSize: LiveData<Size>
        get() = _imageSize

    private val _result = MutableLiveData<MLKitResult>()
    val result: LiveData<MLKitResult>
        get() = _result

    fun onImageSizeChanged(width: Int, height: Int) {
        val imageSize = Size(width, height)
        if (_imageSize.value != imageSize) {
            _imageSize.postValue(imageSize)
        }
    }

    fun onTextResult(result: List<String>) {
        _result.value = MLKitResult.Text(result)
    }

    fun onDrawResult(result: List<DrawObject>) {
        _result.value = MLKitResult.Draw(result)
    }

    fun onDrawBarcode(result: List<FirebaseVisionBarcode>) {
        _result.value = MLKitResult.Barcode(result)
    }

    fun onDrawFace(result: List<FirebaseVisionFace>) {
        _result.value = MLKitResult.Face(result)
    }

    fun onDrawElement(result: List<FirebaseVisionText.Element>) {
        _result.value = MLKitResult.Element(result)
    }
}
