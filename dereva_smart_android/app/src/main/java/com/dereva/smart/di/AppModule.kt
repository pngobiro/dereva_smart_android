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
import com.dereva.smart.data.repository.QuizRepositoryImpl
import com.dereva.smart.data.repository.SchoolRepositoryImpl
import com.dereva.smart.domain.repository.AITutorRepository
import com.dereva.smart.domain.repository.AuthRepository
import com.dereva.smart.domain.repository.ContentRepository
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.PaymentRepository
import com.dereva.smart.domain.repository.ProgressRepository
import com.dereva.smart.domain.repository.QuizRepository
import com.dereva.smart.domain.repository.SchoolRepository
import com.dereva.smart.ui.screens.auth.AuthViewModel
import com.dereva.smart.ui.screens.content.ContentViewModel
import com.dereva.smart.ui.screens.mocktest.MockTestViewModel
import com.dereva.smart.ui.screens.payment.PaymentViewModel
import com.dereva.smart.ui.screens.progress.ProgressViewModel
import com.dereva.smart.ui.screens.quiz.QuizViewModel
import com.dereva.smart.ui.screens.school.SchoolViewModel
import com.dereva.smart.ui.screens.tutor.TutorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // Services
    single { AuthService(context = androidContext()) }
    single { ApiClient.apiService }
    single { GeminiService(apiKey = "YOUR_GEMINI_API_KEY") }
    single { MpesaService(context = androidContext()) }
    single { CloudflareR2Service(context = androidContext()) }
    
    // Download Manager (Will need update to not use contentDao)
    single { DownloadManager(context = androidContext()) }
    
    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            context = androidContext()
        )
    }
    
    single<MockTestRepository> {
        MockTestRepositoryImpl(
            apiService = get()
        )
    }
    
    single<ProgressRepository> {
        ProgressRepositoryImpl(
            apiService = get()
        )
    }
    
    single<AITutorRepository> {
        AITutorRepositoryImpl(
            geminiService = get()
        )
    }
    
    single<SchoolRepository> {
        SchoolRepositoryImpl(
            progressRepository = get(),
            mockTestRepository = get(),
            apiService = get()
        )
    }
    
    single<PaymentRepository> {
        PaymentRepositoryImpl(
            context = androidContext(),
            apiService = get()
        )
    }
    
    single<ContentRepository> {
        ContentRepositoryImpl(
            downloadManager = get(),
            apiService = get()
        )
    }
    
    single<QuizRepository> {
        QuizRepositoryImpl(
            apiService = get()
        )
    }
    
    // ViewModels
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel { MockTestViewModel(repository = get()) }
    viewModel { QuizViewModel(repository = get()) }
    viewModel { ProgressViewModel(progressRepository = get(), mockTestRepository = get()) }
    viewModel { TutorViewModel(repository = get()) }
    viewModel { SchoolViewModel(repository = get()) }
    viewModel { (userId: String) -> PaymentViewModel(paymentRepository = get(), authRepository = get(), userId = userId) }
    viewModel { (userId: String) -> ContentViewModel(repository = get(), userId = userId) }
}
