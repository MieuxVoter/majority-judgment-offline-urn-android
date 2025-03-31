package com.illiouchine.jm

import android.app.Application
import androidx.room.Room
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.data.SqlitePollDataSource
import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.PollDataBase
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.OnBoardingViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.DefaultNavigator
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MajorityUrnApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MajorityUrnApplication)
            modules(module)
        }
    }
}

val module = module {
    // DataBase
    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = PollDataBase::class.java,
            name = "PollDataBase",
        ).build()
    }
    single<PollDao> {
        val dataBase = get<PollDataBase>()
        dataBase.pollDao()
    }

    // Data
    single { SharedPrefsHelper(get()) }
    //single<PollDataSource>(named("inMemory") { InMemoryPollDataSource() }
    single<PollDataSource> { SqlitePollDataSource(get()) }


    // Navigation
    single<Navigator> { DefaultNavigator(Screens.Home) }

    // ViewModel
    viewModel {
        HomeViewModel(
            pollDataSource = get(),
            navigator = get(),
            sharedPrefsHelper = get()
        )
    }
    viewModel {
        SettingsViewModel(
            sharedPreferences = get(),
            navigator = get()
        )
    }
    viewModel {
        PollSetupViewModel(
            sharedPrefsHelper = get(),
            pollDataSource = get(),
            navigator = get(),
        )
    }
    viewModel {
        PollVotingViewModel(
            pollDataSource = get(),
            sharedPrefsHelper = get(),
            navigator = get(),
        )
    }
    viewModel {
        PollResultViewModel(
            pollDataSource = get(),
            navigator = get(),
        )
    }
    viewModel {
        OnBoardingViewModel(
            prefsHelper = get(),
            navigator = get()
        )
    }
}