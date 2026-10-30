package com.illiouchine.jm

import android.app.Application
import androidx.room.Room
import com.illiouchine.jm.data.HardcodedPollTemplateDataSource
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.PollTemplateDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.data.SqlitePollDataSource
import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.PollDataBase
import com.illiouchine.jm.logic.BallotsQrExportViewModel
import com.illiouchine.jm.logic.BallotsQrImportViewModel
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.OnboardingViewModel
import com.illiouchine.jm.logic.PollQrExportViewModel
import com.illiouchine.jm.logic.PollQrImportViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.service.ExchangeUriService
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
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
    // Database
    single<PollDataBase> {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = PollDataBase::class.java,
            name = "PollDataBase",
        ).build()
    }
    // Data Access Object (i.e. Repository)
    single<PollDao> {
        val dataBase = get<PollDataBase>()
        dataBase.pollDao()
    }
    // Data Source
    single<PollDataSource> { SqlitePollDataSource(pollDao = get()) }
    single<PollDataSource>(qualifier = named("inMemory")) { InMemoryPollDataSource() }
    single<PollTemplateDataSource> { HardcodedPollTemplateDataSource() }

    // Config Data
    single { SharedPrefsHelper(context = get()) }

    // Miscellaneous
    single {
        ExchangeUriService(
            // Using the modern https:// scheme
            scheme = "https",
            domain = "mju.mieuxvoter.fr",
            pollRoutePathSegment = "p",
            ballotsRoutePathSegment = "b",
        )
    }
    single(qualifier = named("LegacyExchangeUriService")) {
        ExchangeUriService(
            // Legacy mju:// scheme
            scheme = "mju",
            domain = "",
            pollRoutePathSegment = "p",
            ballotsRoutePathSegment = "b",
        )
    }

    // View Models
    viewModel {
        HomeViewModel(
            pollDataSource = get(),
            pollTemplateDataSource = get(),
            sharedPrefsHelper = get(),
            application = get(),
        )
    }
    viewModel {
        SettingsViewModel(
            sharedPreferences = get(),
        )
    }
    viewModel {
        PollSetupViewModel(
            sharedPrefs = get(),
            pollDataSource = get(),
            pollTemplateDataSource = get(),
            application = get(),
        )
    }
    viewModel {
        PollVotingViewModel(
            pollDataSource = get(),
            sharedPrefsHelper = get(),
        )
    }
    viewModel {
        PollResultViewModel(
            pollDataSource = get(),
            sharedPrefsHelper = get(),
        )
    }
    viewModel {
        PollQrExportViewModel(
            pollDataSource = get(),
            exchangeUriService = get(),
        )
    }
    viewModel {
        PollQrImportViewModel(
            pollDataSource = get(),
            exchangeUriService = get(),
        )
    }
    viewModel {
        BallotsQrExportViewModel(
            pollDataSource = get(),
            exchangeUriService = get(),
        )
    }
    viewModel {
        BallotsQrImportViewModel(
            pollDataSource = get(),
            exchangeUriService = get(),
        )
    }
    viewModel {
        OnboardingViewModel(
            prefsHelper = get(),
        )
    }
}
