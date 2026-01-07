package com.duyvv.basecompose.di

import com.duyvv.basecompose.data.local.dao.TransactionDao
import com.duyvv.basecompose.data.local.database.AppDatabase
import com.duyvv.basecompose.data.local.datastore.ConfigManager
import com.duyvv.basecompose.data.repository.TransactionRepositoryImpl
import com.duyvv.basecompose.domain.repository.TransactionRepository
import com.duyvv.basecompose.presentation.ui.lfo.LFOViewModel
import com.duyvv.basecompose.presentation.ui.onboarding.OnboardingViewModel
import com.duyvv.basecompose.presentation.ui.splash.SplashViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = listOf(
    repositoryModule,
    viewModelModule,
    dataModule,
    helperModule,
    presentationCommonModule
)

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::LFOViewModel)
    viewModelOf(::OnboardingViewModel)
}

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
}

val dataModule = module {
    single<AppDatabase> { AppDatabase.getInstance(get()) }
    single<TransactionDao> { get<AppDatabase>().transactionDao() }
}

val helperModule = module {
    factoryOf(::ConfigManager)
}

val presentationCommonModule = module {
}