package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.fallahpoor.releasetracker.R
import kotlinx.android.synthetic.main.fragment_delete_confirmation.*

class DeleteConfirmationDialog : BottomSheetDialogFragment() {

    interface Listener {

        fun cancelClicked()

        fun deleteClicked()

    }

    private var listener: Listener? = null

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
            listener?.cancelClicked()
        }
        deleteButton.setOnClickListener {
            listener?.deleteClicked()
        }
    }

    internal fun setListener(listener: Listener) {
        this.listener = listener
    }

}