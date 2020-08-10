package soup.mlkit.sample

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import org.tensorflow.lite.Interpreter
import soup.mlkit.sample.camera.CameraActivity
import timber.log.Timber
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

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
            val conditions = FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build()
            FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                            .addOnCompleteListener { task ->
                                val modelFile = task.result
                                interpreter = if (modelFile != null) {
                                    Interpreter(modelFile)
                                } else {
                                    val model =
                                        assets.open("mobilenet_v1_1.0_224_quant.tflite").readBytes()
                                    val buffer = ByteBuffer.allocateDirect(model.size)
                                        .order(ByteOrder.nativeOrder())
                                    buffer.put(model)
                                    Interpreter(buffer)
                                }
                            }
                    } else {
                        val model = assets.open("mobilenet_v1_1.0_224_quant.tflite").readBytes()
                        val buffer =
                            ByteBuffer.allocateDirect(model.size).order(ByteOrder.nativeOrder())
                        buffer.put(model)
                        interpreter = Interpreter(buffer)
                    }
                }
        }

        fun detect(image: FirebaseVisionImage) {
            val interpreter = interpreter ?: return

            val bitmap = Bitmap.createScaledBitmap(image.bitmap, 224, 224, true)
            val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 1).order(ByteOrder.nativeOrder())
            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val px = bitmap.getPixel(x, y)

                    // Get channel values from the pixel value.
                    val r = Color.red(px)
                    val g = Color.green(px)
                    val b = Color.blue(px)

                    // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                    // For example, some models might require values to be normalized to the range
                    // [0.0, 1.0] instead.
                    val rf = (r - 127) / 255
                    val gf = (g - 127) / 255
                    val bf = (b - 127) / 255

                    input.put(rf.toByte())
                    input.put(gf.toByte())
                    input.put(bf.toByte())
                }
            }
            val output = Array(1) { ByteArray(1001) }
            interpreter.run(input, output)

            //outputs를 HashMap으로 변환
            val hashMap: MutableMap<Int, Int> = HashMap()
            val inArray = output[0]
            for (i in inArray.indices) {
                hashMap[i] = inArray[i].toInt()
            }

            //신뢰도순 정렬
            hashMap.entries.maxBy { it.value }?.let {
                onClassified(listOf(ImageLabel(labels[it.key], it.value)))
            }
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
            val probabilities: Int
        )
    }
}
