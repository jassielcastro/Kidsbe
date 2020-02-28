package com.ajcm.kidstube

import android.app.Application
import com.ajcm.data.api.FirebaseApi
import com.ajcm.data.source.UserRemoteSource
import com.ajcm.data.source.YoutubeRemoteSource
import com.ajcm.data.database.LocalDB
import com.ajcm.data.repository.UserRepository
import com.ajcm.data.repository.VideoRepository
import com.ajcm.data.source.FireBaseDataSource
import com.ajcm.data.source.RemoteDataSource
import com.ajcm.domain.User
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.ui.dashboard.DashboardFragment
import com.ajcm.kidstube.ui.dashboard.DashboardViewModel
import com.ajcm.kidstube.ui.playvideo.PlayVideoFragment
import com.ajcm.kidstube.ui.playvideo.PlayVideoViewModel
import com.ajcm.kidstube.ui.profile.ProfileFragment
import com.ajcm.kidstube.ui.profile.ProfileViewModel
import com.ajcm.kidstube.ui.search.SearchFragment
import com.ajcm.kidstube.ui.search.SearchViewModel
import com.ajcm.kidstube.ui.settings.SettingsFragment
import com.ajcm.kidstube.ui.settings.SettingsViewModel
import com.ajcm.kidstube.ui.splash.SplashFragment
import com.ajcm.kidstube.ui.splash.SplashViewModel
import com.ajcm.usecases.GetUserProfile
import com.ajcm.usecases.GetYoutubeVideos
import com.ajcm.usecases.SaveUser
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
        modules(
            listOf(
                appModule,
                youtubeModule,
                userModule,
                dataModule,
                scopesModule)
        )
    }
}

private val appModule = module {
    single(named("apiKey")) { androidApplication().getString(R.string.api_key) }
    single { LocalDB(get()) }
    single { GoogleCredential(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
}

private val youtubeModule = module {
    factory<RemoteDataSource>(named("youtube")) { YoutubeRemoteSource(get()) }
}

private val userModule = module {
    single (named("user_info")) { FirebaseApi.User.Info }
    factory<FireBaseDataSource<User?>>(named("user_data_source")) {
        UserRemoteSource(
            get(
                named("user_info")
            )
        )
    }
}

val dataModule = module {
    factory { VideoRepository(get(named("youtube")), get(named("apiKey"))) }
    factory { UserRepository(get(named("user_data_source"))) }
}

private val scopesModule = module {
    scope(named<SplashFragment>()) {
        viewModel { SplashViewModel( get(), get(), get()) }
        scoped { SaveUser(get()) }
    }
    scope(named<DashboardFragment>()) {
        viewModel { DashboardViewModel(get(), get(), get(), get()) }
        scoped { GetYoutubeVideos(get()) }
        scoped { GetUserProfile(get()) }
    }
    scope(named<PlayVideoFragment>()) {
        viewModel { PlayVideoViewModel( get(), get(), get()) }
        scoped { GetYoutubeVideos(get()) }
    }
    scope(named<ProfileFragment>()) {
        viewModel { ProfileViewModel( get()) }
    }
    scope(named<SearchFragment>()) {
        viewModel { SearchViewModel( get()) }
    }
    scope(named<SettingsFragment>()) {
        viewModel { SettingsViewModel( get()) }
    }
}