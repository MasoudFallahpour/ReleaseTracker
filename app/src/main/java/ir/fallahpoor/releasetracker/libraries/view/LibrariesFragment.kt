package ir.fallahpoor.releasetracker.libraries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import kotlinx.android.synthetic.main.fragment_libraries.*

@AndroidEntryPoint
class LibrariesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_libraries, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addLibraryButton.setOnClickListener {
            findNavController().navigate(R.id.action_librariesFragment_to_addLibraryFragment)
        }
    }

}