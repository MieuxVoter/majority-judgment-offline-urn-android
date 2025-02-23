package com.illiouchine.jm

import android.app.Application
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
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
    // Data
    singleOf( ::SharedPrefsHelper )
    singleOf( ::InMemoryPollDataSource )

    // ViewModel
    viewModel { HomeViewModel(pollDataSource = get()) }
    viewModel { SettingsViewModel(sharedPreferences = get()) }
    viewModel { PollSetupViewModel() }
    viewModel { PollVotingViewModel() }
    viewModel { PollResultViewModel() }
}