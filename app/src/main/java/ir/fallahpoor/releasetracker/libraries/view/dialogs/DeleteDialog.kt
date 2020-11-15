package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.fallahpoor.releasetracker.databinding.DialogDeleteBinding

class DeleteDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
    }

    interface DeleteListener {

        fun cancelClicked(dialogFragment: DialogFragment)

        fun deleteClicked(dialogFragment: DialogFragment)

    }

    private var deleteListener: DeleteListener? = null
    private var _binding: DialogDeleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViews() {
        binding.cancelButton.setOnClickListener {
            deleteListener?.cancelClicked(this)
        }
        binding.deleteButton.setOnClickListener {
            deleteListener?.deleteClicked(this)
        }
    }

    fun setListener(deleteListener: DeleteListener) {
        this.deleteListener = deleteListener
    }

}