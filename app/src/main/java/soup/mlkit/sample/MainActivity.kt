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
            R.id.button_01 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_02 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_03 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_04 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_05 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_06 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_07 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_08 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_09 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_10 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_11 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_12 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_13 -> startActivity(Intent(this, BarcodeDetectActivity::class.java))
            R.id.button_14 -> startActivity(Intent(this, DummyActivity::class.java))
        }
    }
}