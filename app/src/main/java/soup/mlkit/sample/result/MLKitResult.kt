package soup.mlkit.sample.result

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.text.FirebaseVisionText

sealed class MLKitResult {

    data class Text(
        val list: List<String>
    ) : MLKitResult()

    data class Face(
        val list: List<FirebaseVisionFace>
    ) : MLKitResult()

    data class Element(
        val list: List<FirebaseVisionText.Element>
    ) : MLKitResult()

    data class Barcode(
        val list: List<FirebaseVisionBarcode>
    ) : MLKitResult()

    data class Draw(
        val list: List<DrawObject>
    ) : MLKitResult()
}

sealed class DrawObject {

    data class BitmapObject(
        val bitmap: Bitmap,
        val boundingBox: String
    ) : DrawObject()

    data class PointObject(
        val points: List<Point>,
        val boundingBox: String
    ) : DrawObject()

    data class TextObject(
        val text: String,
        val boundingBox: Rect
    ) : DrawObject()
}
