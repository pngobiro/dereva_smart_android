package com.dereva.smart.di

import androidx.room.Room
import com.dereva.smart.data.download.DownloadManager
import com.dereva.smart.data.local.DerevaDatabase
import com.dereva.smart.data.remote.ApiClient
import com.dereva.smart.data.remote.AuthService
import com.dereva.smart.data.remote.CloudflareR2Service
import com.dereva.smart.data.remote.GeminiService
import com.dereva.smart.data.remote.MpesaService
import com.dereva.smart.data.repository.AITutorRepositoryImpl
import com.dereva.smart.data.repository.AuthRepositoryImpl
import com.dereva.smart.data.repository.ContentRepositoryImpl
import com.dereva.smart.data.repository.MockTestRepositoryImpl
import com.dereva.smart.data.repository.PaymentRepositoryImpl
import com.dereva.smart.data.repository.ProgressRepositoryImpl
import com.dereva.smart.data.repository.SchoolRepositoryImpl
import com.dereva.smart.domain.repository.AITutorRepository
import com.dereva.smart.domain.repository.AuthRepository
import com.dereva.smart.domain.repository.ContentRepository
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.PaymentRepository
import com.dereva.smart.domain.repository.ProgressRepository
import com.dereva.smart.domain.repository.SchoolRepository
import com.dereva.smart.ui.screens.auth.AuthViewModel
import com.dereva.smart.ui.screens.content.ContentViewModel
import com.dereva.smart.ui.screens.mocktest.MockTestViewModel
import com.dereva.smart.ui.screens.payment.PaymentViewModel
import com.dereva.smart.ui.screens.progress.ProgressViewModel
import com.dereva.smart.ui.screens.school.SchoolViewModel
import com.dereva.smart.ui.screens.tutor.TutorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            DerevaDatabase::class.java,
            DerevaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    // DAOs
    single { get<DerevaDatabase>().authDao() }
    single { get<DerevaDatabase>().questionDao() }
    single { get<DerevaDatabase>().mockTestDao() }
    single { get<DerevaDatabase>().progressDao() }
    single { get<DerevaDatabase>().tutorDao() }
    single { get<DerevaDatabase>().schoolDao() }
    single { get<DerevaDatabase>().paymentDao() }
    single { get<DerevaDatabase>().contentDao() }
    
    // Services
    single { AuthService(context = androidContext()) }
    single { ApiClient.apiService }
    single { GeminiService(apiKey = "YOUR_GEMINI_API_KEY") }
    single { MpesaService(context = androidContext()) }
    single { CloudflareR2Service(context = androidContext()) }
    
    // Download Manager
    single { DownloadManager(context = androidContext(), contentDao = get()) }
    
    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            authDao = get(),
            authService = get(),
            context = androidContext()
        )
    }
    
    // Repositories
    single<MockTestRepository> {
        MockTestRepositoryImpl(
            questionDao = get(),
            mockTestDao = get(),
            apiService = get()
        )
    }
    
    single<ProgressRepository> {
        ProgressRepositoryImpl(
            progressDao = get()
        )
    }
    
    single<AITutorRepository> {
        AITutorRepositoryImpl(
            tutorDao = get(),
            geminiService = get()
        )
    }
    
    single<SchoolRepository> {
        SchoolRepositoryImpl(
            schoolDao = get(),
            progressRepository = get(),
            mockTestRepository = get()
        )
    }
    
    single<PaymentRepository> {
        PaymentRepositoryImpl(
            context = androidContext(),
            paymentDao = get(),
            mpesaService = get(),
            apiService = get()
        )
    }
    
    single<ContentRepository> {
        ContentRepositoryImpl(
            contentDao = get(),
            downloadManager = get(),
            apiService = get()
        )
    }
    
    // ViewModels
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel { MockTestViewModel(repository = get()) }
    viewModel { ProgressViewModel(progressRepository = get(), mockTestRepository = get()) }
    viewModel { TutorViewModel(repository = get()) }
    viewModel { SchoolViewModel(repository = get()) }
    viewModel { (userId: String) -> PaymentViewModel(paymentRepository = get(), userId = userId) }
    viewModel { (userId: String) -> ContentViewModel(repository = get(), userId = userId) }
}
