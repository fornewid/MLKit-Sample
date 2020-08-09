package soup.mlkit.sample

import com.google.mlkit.vision.common.InputImage

interface OnDetectedListener {
    fun onDetected(input: InputImage)
}
