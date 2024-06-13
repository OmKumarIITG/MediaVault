package com.example.mediavault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mediavault.ui.model.ConverterViewModel.ConverterViewModel
import com.example.mediavault.ui.model.HomeViewModel.HomeViewModel
import com.example.mediavault.ui.model.YoutubeViewModel.YoutubeViewModel
import com.example.mediavault.ui.screens.ConverterScreen
import com.example.mediavault.ui.screens.ConverterSubScreens.CommonScreenConverter
import com.example.mediavault.ui.screens.HomeScreen
import com.example.mediavault.ui.screens.YoutubeDownloaderScreen
import dagger.hilt.android.AndroidEntryPoint

data class BottomNavigationItem(
    val title:String,
    val selectedIcon: ImageVector,
    val unselectedIcon : ImageVector,
    val route : String
)

enum class Screens {
    HOME,
    YOUTUBE_DOWNLOADER,
    VIDEO_CONVERTER,
    VIDEO_TO_GIF,
    VIDEO_TO_AUDIO
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val youtubeViewModel = hiltViewModel<YoutubeViewModel>()
            val items = listOf(
                BottomNavigationItem(
                    "Home",
                    Icons.Filled.Home,
                    Icons.Outlined.Home,
                    Screens.HOME.name
                ),
                BottomNavigationItem(
                    "Youtube Downloader",
                    Icons.Filled.Download,
                    Icons.Outlined.Download,
                    Screens.YOUTUBE_DOWNLOADER.name
                ),
                BottomNavigationItem(
                    "Video Converter",
                    Icons.Filled.Construction,
                    Icons.Default.Construction,
                    Screens.VIDEO_CONVERTER.name
                )
            )

            var selectedItemIndex by remember {
                mutableIntStateOf(0)
            }

            Scaffold (
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                          },
                                label = {Text(item.title)},
                                icon = {
                                    Icon(
                                        imageVector = if(selectedItemIndex == index) item.selectedIcon
                                        else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            )
                        }
                    }
                }
            ){it->
                Column(
                    modifier= Modifier.padding(it)
                ){
                    NavHost(navController = navController, startDestination = "home") {
                        composable(Screens.HOME.name){
                            HomeScreen(homeViewModel)
                        }
                        composable(Screens.YOUTUBE_DOWNLOADER.name){
                            YoutubeDownloaderScreen(youtubeViewModel)
                        }
                        composable(Screens.VIDEO_CONVERTER.name){
                            ConverterScreen(navController = navController)
                        }
                        composable(Screens.VIDEO_TO_GIF.name){
                            val converterViewModel = hiltViewModel<ConverterViewModel>()
                            CommonScreenConverter(converterViewModel = converterViewModel, screenCode = 1) {
                                navController.navigateUp()
                            }
                        }
                        composable(Screens.VIDEO_TO_AUDIO.name){
                            val converterViewModel = hiltViewModel<ConverterViewModel>()
                            CommonScreenConverter(converterViewModel = converterViewModel, screenCode = 2) {
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}
