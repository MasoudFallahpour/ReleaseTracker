package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.android.synthetic.main.sorting_order_dialog.*
import javax.inject.Inject

@AndroidEntryPoint
class SortingOrderDialogFragment : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "SortingOrderDialogFragment"
    }

    interface SortListener {
        fun orderSelected(sortingOrder: SortingOrder)
    }

    enum class SortingOrder {
        A_TO_Z,
        Z_TO_A,
        PINNED_FIRST
    }

    @Inject
    lateinit var localStorage: LocalStorage
    private var listener: SortListener? = null
    private var defaultSortingOrder: SortingOrder = SortingOrder.A_TO_Z

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sortingOrder = localStorage.getSortingOrder()
        defaultSortingOrder = if (sortingOrder != null) {
            SortingOrder.valueOf(sortingOrder)
        } else {
            SortingOrder.A_TO_Z
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.sorting_order_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() {

        val onClickListener = View.OnClickListener { view: View ->
            val sortingOrder = when (view.id) {
                R.id.sortingOrderAtoZTextView -> SortingOrder.A_TO_Z
                R.id.sortingOrderZtoATextView -> SortingOrder.Z_TO_A
                R.id.sortingOrderPinnedFirstTextView -> SortingOrder.PINNED_FIRST
                else -> SortingOrder.A_TO_Z
            }
            listener?.orderSelected(sortingOrder)
            dismiss()
        }

        when (defaultSortingOrder) {
            SortingOrder.A_TO_Z -> sortingOrderAtoZTextView.isSelected = true
            SortingOrder.Z_TO_A -> sortingOrderZtoATextView.isSelected = true
            SortingOrder.PINNED_FIRST -> sortingOrderPinnedFirstTextView.isSelected = true
        }

        sortingOrderAtoZTextView.setOnClickListener(onClickListener)
        sortingOrderZtoATextView.setOnClickListener(onClickListener)
        sortingOrderPinnedFirstTextView.setOnClickListener(onClickListener)

    }

    fun setListener(listener: SortListener) {
        this.listener = listener
    }

}