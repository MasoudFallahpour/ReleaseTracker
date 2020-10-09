package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.android.synthetic.main.dialog_sort.*
import javax.inject.Inject

@AndroidEntryPoint
class SortDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "SortDialog"
    }

    interface SortListener {
        fun orderSelected(order: Order)
    }

    enum class Order {
        A_TO_Z,
        Z_TO_A,
        PINNED_FIRST
    }

    @Inject
    lateinit var localStorage: LocalStorage
    private var listener: SortListener? = null
    private var defaultOrder: Order = Order.A_TO_Z

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sortingOrder = localStorage.getSortingOrder()
        defaultOrder = if (sortingOrder != null) {
            Order.valueOf(sortingOrder)
        } else {
            Order.A_TO_Z
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_sort, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() {

        val onClickListener = View.OnClickListener { view: View ->
            val order = when (view.id) {
                R.id.sortingOrderAtoZTextView -> Order.A_TO_Z
                R.id.sortingOrderZtoATextView -> Order.Z_TO_A
                R.id.sortingOrderPinnedFirstTextView -> Order.PINNED_FIRST
                else -> Order.A_TO_Z
            }
            listener?.orderSelected(order)
            dismiss()
        }

        when (defaultOrder) {
            Order.A_TO_Z -> sortingOrderAtoZTextView.isSelected = true
            Order.Z_TO_A -> sortingOrderZtoATextView.isSelected = true
            Order.PINNED_FIRST -> sortingOrderPinnedFirstTextView.isSelected = true
        }

        sortingOrderAtoZTextView.setOnClickListener(onClickListener)
        sortingOrderZtoATextView.setOnClickListener(onClickListener)
        sortingOrderPinnedFirstTextView.setOnClickListener(onClickListener)

    }

    fun setListener(listener: SortListener) {
        this.listener = listener
    }

}