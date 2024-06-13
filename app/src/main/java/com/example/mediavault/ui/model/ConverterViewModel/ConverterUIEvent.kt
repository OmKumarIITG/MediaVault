package com.example.mediavault.ui.model.ConverterViewModel

import android.net.Uri

sealed class ConverterUIEvent {
    data class LauncherLaunched(val uri: Uri?) : ConverterUIEvent()
    data class IsDefaultButtonSelected(val default : Boolean) : ConverterUIEvent()
    data class StartFromChanged(val start:String?):ConverterUIEvent()
    data class TrimDurationChanged(val trim:String?) : ConverterUIEvent()
    data class FrameRateChanged(val fps:String?):ConverterUIEvent()
    data object VTGIFExecutionStart : ConverterUIEvent()
    data object VTAUDIOExecutionStart : ConverterUIEvent()
}