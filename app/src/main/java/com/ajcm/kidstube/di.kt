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

const val API_KEY = "api_Key"

const val FIREBASE_VIDEOS = "firebase_videos"
const val FIREBASE_USER = "firebase_user"
const val FIREBASE_USER_VIDEOS = "firebase_user_videos_watched"

const val SOURCE_YOUTUBE = "source_youtube"
const val SOURCE_USER = "source_user"
const val SOURCE_USER_VIDEOS = "source_user_videos"

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(
            listOf(
                appModule,
                firebaseModule,
                youtubeModule,
                userModule,
                repositoryModule,
                scopesModule)
        )
    }
}

private val appModule = module {
    single(named(API_KEY)) { androidApplication().getString(R.string.api_key) }
    single { KidstubeDB.build(get()) }
    factory<LocalDataSource<User>> { RoomDataSource(get()) }
    single { GoogleCredential(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
}

private val firebaseModule = module {
    single (named(FIREBASE_USER)) { FirebaseApi.User.Info }
    single (named(FIREBASE_VIDEOS)) { FirebaseApi.Videos.Watched }
    single (named(FIREBASE_USER_VIDEOS)) { (userId: String) -> FirebaseApi.User.VideoWatched(userId) }
}

private val youtubeModule = module {
    factory<RemoteDataSource>(named(SOURCE_YOUTUBE)) { YoutubeRemoteSource(get(), get(named(FIREBASE_VIDEOS))) }
}

private val userModule = module {
    factory<FireBaseDataSource<User?>>(named(SOURCE_USER)) {
        UserRemoteSource(get(named(FIREBASE_USER)))
    }

    factory<FireBaseDataSource<Video?>>(named(SOURCE_USER_VIDEOS)) { (userId: String) ->
        VideoWatchedDataSource(get(named(FIREBASE_USER_VIDEOS)) { parametersOf(userId) })
    }
}

val repositoryModule = module {
    factory { VideoRepository(get(named(SOURCE_YOUTUBE)), get(named(API_KEY))) }
    factory { UserRepository(get(named(SOURCE_USER))) }
    factory { (userId: String) ->
        VideoWatchedRepository(get(named(SOURCE_USER_VIDEOS)) {
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
        viewModel { SearchViewModel( get(), get(), get()) }
        scoped { SearchYoutubeVideos(get()) }
    }
    scope(named<SettingsFragment>()) {
        viewModel { SettingsViewModel( get(), get(), get()) }
        scoped { UpdateUser(get()) }
    }
}