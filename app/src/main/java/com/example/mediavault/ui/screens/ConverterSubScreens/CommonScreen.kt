package com.example.mediavault.ui.screens.ConverterSubScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthenica.mobileffmpeg.Config
import com.example.mediavault.ui.model.ConverterViewModel.ConverterUIEvent
import com.example.mediavault.ui.model.ConverterViewModel.ConverterViewModel

@Composable
fun CommonScreenConverter(
    converterViewModel: ConverterViewModel,
    screenCode : Int, // 1 for to gif, 2 for to audio, 3 for to compress
    onBackClick : ()->Unit
) {
    //declare launcher to select video
    val selectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        //contract: what we want to do with this activity
        //result will be our chosen video
        onResult ={uri->
            converterViewModel.onEvent(ConverterUIEvent.LauncherLaunched(uri))
        }
    )

    //some state values
    val state = converterViewModel.uiState.collectAsState()
    val fileName = state.value.fileName
    val isDefaultSelected = state.value.isDefaultSelected
    val statistics = state.value.callbackStatistics
    val isExecuting = state.value.isExecuting
    val executionStatus = state.value.executionStatus

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when (screenCode) {
            1 -> {Header(heading = "VIDEO TO GIF CONVERSION",onBackClick)}
            2 -> {Header(heading = "VIDEO TO AUDIO CONVERSION",onBackClick)}
            else -> {Header(heading = "VIDEO COMPRESSION",onBackClick)}
        }
        Row(
            modifier=Modifier.fillMaxWidth()
        ){
            Text("Please Select Video : ",Modifier.padding(top=15.dp))
            IconButton(onClick = { selectVideoLauncher.launch(arrayOf("video/*")) }) {
                Icon(Icons.Default.Attachment,"attach video")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ){
            var videoName = fileName
            if(fileName == null){
                videoName = "No Video Selected"
            }
            Text("Selected Video : $videoName")
        }
        Divider( color = Color.Black,thickness = 1.dp)

        if(screenCode == 1){
            Text("Select Conversion Options")
            Divider( color = Color.Black,thickness = 1.dp)
            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ){
                Text("1. Default",Modifier.padding(top=8.dp))
                RadioButton(
                    selected = isDefaultSelected, onClick = {
                        converterViewModel.onEvent(ConverterUIEvent.IsDefaultButtonSelected(true))
                    })
            }

            Column (
                modifier=Modifier.fillMaxWidth()
            ){
                Row(
                    modifier=Modifier.fillMaxWidth()
                ){
                    Text("2. With Some Optimisation",Modifier.padding(top=15.dp))
                    RadioButton(selected = !isDefaultSelected, onClick = { converterViewModel.onEvent(ConverterUIEvent.IsDefaultButtonSelected(false))})
                }
                InputBasicTextField(
                    "(a) Start From (in seconds) ",
                    {
                        converterViewModel.onEvent(ConverterUIEvent.StartFromChanged(it))
                    },
                    "0"
                )
                Spacer(modifier=Modifier.height(20.dp))
                InputBasicTextField(
                    "(b) Trim Duration (in seconds) ",
                    {
                        converterViewModel.onEvent(ConverterUIEvent.TrimDurationChanged(it))
                    },
                    "2"
                )
                Spacer(modifier=Modifier.height(20.dp))
                InputBasicTextField(
                    "(c) Frame Rate: ",
                    {
                        converterViewModel.onEvent(ConverterUIEvent.FrameRateChanged(it))
                    },
                    "10"
                )
            }
        }
        Spacer(modifier=Modifier.height(20.dp))
        Button(onClick = {
            when(screenCode){
                1->converterViewModel.onEvent(ConverterUIEvent.VTGIFExecutionStart)
                2->converterViewModel.onEvent(ConverterUIEvent.VTAUDIOExecutionStart)
            }
                         },shape = RectangleShape) {
            Text("Start Conversion")
        }
        Spacer(modifier=Modifier.height(10.dp))
        Text(
            "Conversion Status: ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier=Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Box(
            modifier= Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        1.dp, Brush.horizontalGradient(
                            colors = listOf(
                                Color.Blue,
                                Color.Red
                            )
                        )
                    )
                )
        ){
            if(statistics.toString().isEmpty()){
                Text("No Statistics",Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }else{
                Text(
                    statistics.toString(),
                    Modifier.verticalScroll(rememberScrollState())
                )
            }
        }
        if(isExecuting){
            StatusBar(status = "PLEASE WAIT CONVERSION IS GOING ON")
        }else if(!isExecuting && statistics.toString().isNotEmpty()){
            if(executionStatus == Config.RETURN_CODE_SUCCESS){
                StatusBar(status = "FILE SUCCESSFULLY CONVERTED")
            }else if(executionStatus == Config.RETURN_CODE_CANCEL){
                StatusBar(status = "EXECUTION CANCELLED BY USER")
            }else{
                StatusBar(status = "ERROR OCCURRED, SEE EXECUTION STATISTICS")
            }
        }
    }
}

@Composable
fun InputBasicTextField(
    leadingText: String,
    onValueChange:(String)->Unit,
    defaultValue : String
) {
    var text by remember {
        mutableStateOf(defaultValue)
    }

    Row(
        modifier= Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
    ){
        Text(leadingText)
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(text)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            ,modifier = Modifier
                .fillMaxWidth(0.2f)
                .border(1.dp, Color.Black),
            singleLine = true
        )
    }
}

@Composable
fun StatusBar(status : String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow),
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            status,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Header(heading : String , onClick:()->Unit) {
    Row(modifier=Modifier.fillMaxWidth()){
        IconButton(onClick = { onClick() }) {
            Icon(Icons.Filled.ArrowBackIosNew,"back_button")
        }
        Text(
            heading,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun M() {
//    Header(heading = "VIDEO TO GIF CONVERTER") {
//
//    }
//}