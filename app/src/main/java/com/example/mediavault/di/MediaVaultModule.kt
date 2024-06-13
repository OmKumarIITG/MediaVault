package com.example.mediavault.di

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.mediavault.ui.model.HomeViewModel.MetaDataReader
import com.example.mediavault.ui.model.HomeViewModel.MetaDataReaderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MediaVaultModule {

    @Provides
    @ViewModelScoped
    fun getPythonInstance(app:Application):Python{
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform (app))
        }
        return Python.getInstance()
    }

    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): Player {
        return ExoPlayer.Builder(app)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(app: Application): MetaDataReader {
        return MetaDataReaderImpl(app)
    }
}