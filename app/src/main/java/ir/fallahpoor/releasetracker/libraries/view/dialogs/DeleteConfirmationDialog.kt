package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.fallahpoor.releasetracker.R
import kotlinx.android.synthetic.main.fragment_delete_confirmation.*

class DeleteConfirmationDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteConfirmationDialog"
    }

    interface DeleteListener {

        fun cancelClicked(dialogFragment: DialogFragment)

        fun deleteClicked(dialogFragment: DialogFragment)

    }

    private var deleteListener: DeleteListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_delete_confirmation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() {
        cancelButton.setOnClickListener {
            deleteListener?.cancelClicked(this)
        }
        deleteButton.setOnClickListener {
            deleteListener?.deleteClicked(this)
        }
    }

    fun setListener(deleteListener: DeleteListener) {
        this.deleteListener = deleteListener
    }

}