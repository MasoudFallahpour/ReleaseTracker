package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
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

    @Inject
    lateinit var nightModeManager: NightModeManager
    private var listener: SortListener? = null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ReleaseTrackerTheme(darkTheme = nightModeManager.isDarkTheme()) {
                val currentOrder: Order = getCurrentOrder()
                SortScreen(currentOrder)
            }
        }
    }

    private fun getCurrentOrder(): Order {
        val sortingOrder = localStorage.getOrder()
        return if (sortingOrder != null) {
            Order.valueOf(sortingOrder)
        } else {
            Order.A_TO_Z
        }
    }

    @Composable
    private fun SortScreen(currentOrder: Order) {
        Column {
            Text(
                text = stringResource(R.string.select_sorting_order),
                modifier = Modifier.padding(SPACE_NORMAL.dp)
            )
            SortItem(
                text = stringResource(R.string.a_to_z),
                order = Order.A_TO_Z,
                currentOrder = currentOrder
            )
            SortItem(
                text = stringResource(R.string.z_to_a),
                order = Order.Z_TO_A,
                currentOrder = currentOrder
            )
            SortItem(
                text = stringResource(R.string.pinned_first),
                order = Order.PINNED_FIRST,
                currentOrder = currentOrder
            )
        }
    }

    @Composable
    private fun SortItem(text: String, order: Order, currentOrder: Order) {
        val textColor = if (currentOrder == order) {
            MaterialTheme.colors.secondary
        } else {
            Color.Unspecified
        }
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        listener?.orderSelected(order)
                        dismiss()
                    })
                .padding(SPACE_SMALL.dp)
        )
    }

    fun setListener(listener: SortListener) {
        this.listener = listener
    }

    @Preview
    @Composable
    private fun SortScreenPreview() {
        SortScreen(Order.PINNED_FIRST)
    }

}