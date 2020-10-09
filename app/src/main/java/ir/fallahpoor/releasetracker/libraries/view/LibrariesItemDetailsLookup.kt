package ir.fallahpoor.releasetracker.libraries.view

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class LibrariesItemDetailsLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<String>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {

        val view = recyclerView.findChildViewUnder(event.x, event.y)

        return if (view != null) {
            (recyclerView.getChildViewHolder(view) as LibrariesAdapter.LibraryViewHolder).getItemDetails()
        } else {
            null
        }

    }

}