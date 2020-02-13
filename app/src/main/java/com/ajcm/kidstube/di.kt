package com.ajcm.kidstube

import android.app.Application
import com.ajcm.data.api.YoutubeApi
import com.ajcm.data.api.YoutubeDataSource
import com.ajcm.data.repository.VideoRepository
import com.ajcm.data.source.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(appModule, dataModule))
    }
}

private val appModule = module {
    single(named("apiKey")) { androidApplication().getString(R.string.api_key) }
    factory<RemoteDataSource> { YoutubeDataSource(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
    single(named("baseUrl")) { "https://www.googleapis.com/youtube/v3/" }
    single { YoutubeApi(get(named("baseUrl"))) }
}

val dataModule = module {
    factory { VideoRepository(get(), get(named("apiKey"))) }
}