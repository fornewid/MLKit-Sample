package soup.mlkit.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.button_01 -> startActivity(Intent(this, LabelDetectorActivity::class.java))
            R.id.button_02 -> startActivity(Intent(this, RealtimeLabelDetectorActivity::class.java))
            R.id.button_03 -> startActivity(Intent(this, FaceDetectorActivity::class.java))
            R.id.button_04 -> startActivity(Intent(this, BarcodeDetectorActivity::class.java))
            R.id.button_05 -> startActivity(Intent(this, LandmarkDetectorActivity::class.java))
            R.id.button_06 -> startActivity(Intent(this, TextRecognizerActivity::class.java))
            R.id.button_07 -> startActivity(Intent(this, CustomModelActivity::class.java))
        }
    }
}