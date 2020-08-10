package soup.mlkit.sample.result

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect

sealed class MLKitResult {

    data class Text(
        val list: List<String>
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
