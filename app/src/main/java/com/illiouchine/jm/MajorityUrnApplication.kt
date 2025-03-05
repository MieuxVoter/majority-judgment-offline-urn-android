package com.illiouchine.jm

import android.app.Application
import androidx.room.Room
import com.illiouchine.jm.data.BDDPollDataSource
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.PollDataBase
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
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
            PollDataBase::class.java,
            "PollDataBase"
        ).build()
    }
    single<PollDao> {
        val dataBase = get<PollDataBase>()
        dataBase.pollDao()
    }

    // Data
    single { SharedPrefsHelper(get()) }
    //single<PollDataSource>(named("inMemory") { InMemoryPollDataSource() }
    single<PollDataSource> { BDDPollDataSource(get()) }

    // ViewModel
    viewModel { HomeViewModel(pollDataSource = get()) }
    viewModel { SettingsViewModel(sharedPreferences = get()) }
    viewModel {
        PollSetupViewModel(
            sharedPrefsHelper = get(),
            pollDataSource = get(),
        )
    }
    viewModel { PollVotingViewModel() }
    viewModel { PollResultViewModel() }

}