package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.android.synthetic.main.dialog_night_mode.*
import javax.inject.Inject

@AndroidEntryPoint
class NightModeDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "SelectNightModeDialog"
    }

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_night_mode, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() {
        val currentMode = NightModeManager.Mode.valueOf(
            localStorage.getNightMode() ?: NightModeManager.Mode.OFF.name
        )
        when (currentMode) {
            NightModeManager.Mode.OFF -> nightModeOffButton.isChecked = true
            NightModeManager.Mode.ON -> nightModeOnButton.isChecked = true
            NightModeManager.Mode.AUTO -> nightModeAutoButton.isChecked = true
        }
        nightModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.nightModeOffButton -> nightModeManager.setNightMode(NightModeManager.Mode.OFF)
                R.id.nightModeOnButton -> nightModeManager.setNightMode(NightModeManager.Mode.ON)
                R.id.nightModeAutoButton -> nightModeManager.setNightMode(NightModeManager.Mode.AUTO)
            }
            dismiss()
        }
    }

}