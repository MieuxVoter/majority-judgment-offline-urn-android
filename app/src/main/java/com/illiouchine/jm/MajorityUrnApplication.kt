package com.illiouchine.jm

import android.app.Application
import androidx.navigation.Navigator
import androidx.room.Room
import com.illiouchine.jm.data.SqlitePollDataSource
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.PollDataBase
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.DefaultNavigator
import com.illiouchine.jm.ui.Destination
import com.illiouchine.jm.ui.Navigator2
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MajorityUrnApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MajorityUrnApplication)
            androidLogger()
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


    // compose
    single<Navigator2> {
        DefaultNavigator(startDestination = Destination.Home)
    }

    // ViewModel
    viewModel { HomeViewModel(
        pollDataSource = get(),
        navigator = get(),
        prefsHelper = get()
    ) }
    viewModel { SettingsViewModel(sharedPreferences = get()) }
    viewModel {
        PollSetupViewModel(
            savedStateHandle = get(),
            sharedPrefsHelper = get(),
            pollDataSource = get(),
            navigator = get(),
        )
    }
    viewModel {
        PollVotingViewModel(
            savedStateHandle = get(),
            pollDataSource = get(),
            sharedPrefsHelper = get(),
            navigator = get(),
        )
    }
    viewModel {
        PollResultViewModel(
            savedStateHandle = get(),
            navigator = get(),
        )
    }
}