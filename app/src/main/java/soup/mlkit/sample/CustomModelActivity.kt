package soup.mlkit.sample

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.ml.custom.FirebaseModelDataType
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions
import com.google.firebase.ml.custom.FirebaseModelInputs
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import org.tensorflow.lite.Interpreter
import soup.mlkit.sample.camera.CameraActivity
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CustomModelActivity : CameraActivity() {

    private var imageClassification: ImageClassification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageClassification = ImageClassification(assets) {
            viewModel.onTextResult(it.map { imageLabel ->
                imageLabel.label + ", confidence=" + imageLabel.probabilities
            })
        }
    }

    override fun onDetected(bitmap: Bitmap) {
        imageClassification?.detect(FirebaseVisionImage.fromBitmap(bitmap))
    }

    private class ImageClassification(
        private val assets: AssetManager,
        private val onClassified: (List<ImageLabel>) -> Unit
    ) {

        private val labels: List<String> =
            InputStreamReader(assets.open("labels_mobilenet_quant_v1_224.txt"))
                .useLines { it.toList() }

        private var interpreter: Interpreter? = null

        init {
            val remoteModel = FirebaseCustomRemoteModel.Builder("image-classification").build()
            FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                .addOnCompleteListener { task ->
                    val modelFile = task.result
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                    } else {
                        val model = assets.open("mobilenet_v1_1.0_224_quant.tflite").readBytes()
                        val buffer = ByteBuffer.allocateDirect(model.size).order(ByteOrder.nativeOrder())
                        buffer.put(model)
                        interpreter = Interpreter(buffer)
                    }
                }
        }

        fun detect(image: FirebaseVisionImage) {
            //TODO:
            val interpreter = interpreter ?: return

            val inputOutputOptions =
                FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.BYTE, intArrayOf(1, 224, 224, 3))
                    .setOutputFormat(0, FirebaseModelDataType.BYTE, intArrayOf(1, 1001))
                    .build()

            val bitmap = Bitmap.createScaledBitmap(image.bitmap, 224, 224, true)
            val input = image2inputData(bitmap, Point(224, 224))
            val inputs = FirebaseModelInputs.Builder()
                .add(input)
                .build()

            interpreter.run(inputs, inputOutputOptions)

            onClassified(emptyList())
        }

        //Bitmap을 다차원 배열로 변환
        private fun image2inputData(image: Bitmap, size: Point): Array<Array<Array<ByteArray>>> {
            //Bitmap 리사이즈와 RGBA int 배열로의 변환
            val rgbaData: IntArray = cgImage2rgbaData(image, size)

            //RGBA int배열을 RGB byte 배열로 변환
            val inputData = Array(1) { Array(size.y) { Array(size.x) { ByteArray(3) } } }
            rgbaData.indices.forEach { i ->
                inputData[0][i / size.x][i % size.x][0] = Color.red(rgbaData[i]).toByte()
                inputData[0][i / size.x][i % size.x][1] = Color.green(rgbaData[i]).toByte()
                inputData[0][i / size.x][i % size.x][2] = Color.blue(rgbaData[i]).toByte()
            }
            return inputData
        }

        //Bitmap을 RGBA int 배열로 변환
        private fun cgImage2rgbaData(image: Bitmap, size: Point): IntArray {
            return Bitmap.createScaledBitmap(image, size.x, size.y, true).let {
                IntArray(size.x * size.y).apply {
                    it.getPixels(this, 0, size.x, 0, 0, size.x, size.y)
                }
            }
        }

        data class ImageLabel(
            val label: String,
            val probabilities: Float
        )
    }
}
