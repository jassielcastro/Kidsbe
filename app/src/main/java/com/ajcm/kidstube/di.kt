package com.ajcm.kidstube

import android.app.Application
import com.ajcm.data.api.YoutubeApi
import com.ajcm.data.api.YoutubeDataSource
import com.ajcm.data.api.YoutubeRemoteSource
import com.ajcm.data.database.LocalDB
import com.ajcm.data.repository.VideoRepository
import com.ajcm.data.source.RemoteDataSource
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.ui.dashboard.DashboardFragment
import com.ajcm.kidstube.ui.dashboard.DashboardViewModel
import com.ajcm.kidstube.ui.splash.SplashFragment
import com.ajcm.kidstube.ui.splash.SplashViewModel
import com.ajcm.usecases.GetYoutubeVideos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(appModule, dataModule, scopesModule))
    }
}

private val appModule = module {
    single(named("apiKey")) { androidApplication().getString(R.string.api_key) }
    single(named("baseUrl")) { "https://www.googleapis.com/youtube/v3/" }
    single { YoutubeApi(get(named("baseUrl"))) }
    single { LocalDB(get()) }
    single { GoogleCredential(get()) }
    factory<RemoteDataSource>(named("retrofit")) { YoutubeDataSource(get()) }
    factory<RemoteDataSource>(named("google")) { YoutubeRemoteSource(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
}

val dataModule = module {
    factory { VideoRepository(get(named("google")), get(named("apiKey"))) }
}

private val scopesModule = module {
    scope(named<SplashFragment>()) {
        viewModel { SplashViewModel( get(), get()) }
    }
    scope(named<DashboardFragment>()) {
        viewModel { DashboardViewModel(get(), get(), get()) }
        scoped { GetYoutubeVideos(get()) }
    }
}