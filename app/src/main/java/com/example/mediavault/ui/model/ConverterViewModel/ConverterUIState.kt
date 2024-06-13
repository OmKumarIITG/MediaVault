package com.example.mediavault.ui.model.ConverterViewModel

import android.net.Uri

data class ConverterUIState(
    val uri : Uri? = null,
    val fileName : String? = null,
    val callbackStatistics : String? = "",
    val isDefaultSelected : Boolean = true,
    val startTime : String? = null,
    val trimDuration : String?="",
    val frameRate : String?="",
    //to check execution status
    val executionStatus : Int? = 1,
    val isExecuting : Boolean = false
)
