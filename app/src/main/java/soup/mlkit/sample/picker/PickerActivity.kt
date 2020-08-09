package soup.mlkit.sample.picker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import soup.mlkit.sample.databinding.PickerBinding
import soup.mlkit.sample.utils.setOnDebounceClickListener

abstract class PickerActivity : AppCompatActivity() {

    private lateinit var binding: PickerBinding

    abstract fun onDetected(uri: Uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pickerButton.setOnDebounceClickListener {
            requestPermissions(STORAGE_PERMISSION, REQUEST_CODE_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (isPermissionsGranted(STORAGE_PERMISSION)) {
                    Gallery.takePicture(this)
                } else {
                    Toast.makeText(applicationContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Gallery.onPictureTaken(requestCode, resultCode, data) { uri ->
            binding.previewView.setImageURI(uri)
            onDetected(uri)
        }
    }

    private fun Context.isPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {

        private const val REQUEST_CODE_STORAGE = 11

        private val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
