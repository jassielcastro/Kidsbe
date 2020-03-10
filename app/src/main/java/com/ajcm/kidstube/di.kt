package com.ajcm.kidstube

import android.app.Application
import com.ajcm.data.api.FirebaseApi
import com.ajcm.data.database.KidstubeDB
import com.ajcm.data.repository.UserRepository
import com.ajcm.data.repository.VideoRepository
import com.ajcm.data.repository.VideoWatchedRepository
import com.ajcm.data.source.*
import com.ajcm.domain.User
import com.ajcm.domain.Video
import com.ajcm.kidstube.common.GoogleCredential
import com.ajcm.kidstube.ui.dashboard.DashboardFragment
import com.ajcm.kidstube.ui.dashboard.DashboardViewModel
import com.ajcm.kidstube.ui.main.MainActivity
import com.ajcm.kidstube.ui.main.MainActivityViewModel
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
import com.ajcm.usecases.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
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
    single { KidstubeDB.build(get()) }
    factory<LocalDataSource<User>> { RoomDataSource(get()) }
    single { GoogleCredential(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
}

private val youtubeModule = module {
    factory<RemoteDataSource>(named("youtube")) { YoutubeRemoteSource(get()) }
}

private val userModule = module {
    single (named("user_info")) { FirebaseApi.User.Info }
    factory<FireBaseDataSource<User?>>(named("user_data_source")) {
        UserRemoteSource(get(named("user_info")))
    }

    single (named("videos_watched")) { (userId: String) -> FirebaseApi.User.VideoWatched(userId) }
    factory<FireBaseDataSource<Video?>>(named("watched_data_source")) { (userId: String) ->
        VideoWatchedDataSource(get(named("videos_watched")) { parametersOf(userId) })
    }
}

val dataModule = module {
    factory { VideoRepository(get(named("youtube")), get(named("apiKey"))) }
    factory { UserRepository(get(named("user_data_source"))) }
    factory { (userId: String) ->
        VideoWatchedRepository(get(named("watched_data_source")) {
            parametersOf(userId)
        })
    }
}

private val scopesModule = module {
    scope(named<MainActivity>()) {
        viewModel { MainActivityViewModel( get(), get()) }
    }
    scope(named<SplashFragment>()) {
        viewModel { SplashViewModel( get(), get(), get(), get()) }
        scoped { SaveUser(get()) }
        scoped { GetUserProfile(get()) }
    }
    scope(named<DashboardFragment>()) {
        viewModel { DashboardViewModel(get(), get(), get()) }
        scoped { GetYoutubeVideos(get()) }
    }
    scope(named<PlayVideoFragment>()) {
        viewModel { (userId: String) -> PlayVideoViewModel( get(), get { parametersOf(userId) }, get(), get(), get()) }
        scoped { GetYoutubeVideos(get()) }
        scoped { (userId: String) -> SaveVideoWatched(get { parametersOf(userId) }) }
        scoped { UpdateUser(get()) }
    }
    scope(named<ProfileFragment>()) {
        viewModel { ProfileViewModel( get(), get(), get()) }
        scoped { UpdateUser(get()) }
    }
    scope(named<SearchFragment>()) {
        viewModel { SearchViewModel( get()) }
    }
    scope(named<SettingsFragment>()) {
        viewModel { SettingsViewModel( get(), get(), get()) }
        scoped { UpdateUser(get()) }
    }
}