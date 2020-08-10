package soup.mlkit.sample.result

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import soup.mlkit.sample.R
import soup.mlkit.sample.databinding.ResultBinding

class MLKitResultFragment : Fragment(R.layout.result) {

    private val viewModel: MLKitResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ResultBinding.bind(view)

        viewModel.imageSize.observe(viewLifecycleOwner, Observer {
            binding.drawView.setImageSize(it.width, it.height)
        })
        viewModel.result.observe(viewLifecycleOwner, Observer {
            binding.drawView.isVisible = it is MLKitResult.Draw
            binding.textScrollView.isVisible = it is MLKitResult.Text

            when (it) {
                is MLKitResult.Text ->
                    if (it.list.isNotEmpty()) {
                        binding.text.text = it.list.joinToString(separator = "\n")
                    }
                is MLKitResult.Draw -> {
                    //TODO:
                }
            }
        })
    }
}
