package com.example.mediavault.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mediavault.Screens

@Composable
fun ConverterScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Choose Conversion Option",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            modifier=Modifier.padding(vertical = 5.dp)
        )
        ConverterItem(conversionTitle = "Video To GIF Converter") {
            navController.navigate(Screens.VIDEO_TO_GIF.name)
        }
        ConverterItem(conversionTitle = "Video To Audio Converter") {
            navController.navigate(Screens.VIDEO_TO_AUDIO.name)
        }
    }
}

@Composable
fun ConverterItem(
    conversionTitle : String,
    onClick:()->Unit
) {
    Button(
        onClick = { onClick() },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 2.dp,
            focusedElevation = 1.dp,
        ),
        shape = RectangleShape,
        modifier=Modifier.fillMaxWidth(0.8f),
        border = BorderStroke(4.dp, Brush.horizontalGradient(
            colors = listOf(
                Color.Red,
                Color.Blue
            )
        )),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Yellow,
            contentColor = Color.Black
        )
    ) {
        Text(
            conversionTitle
        )
    }
}
