package com.dereva.smart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dereva.smart.domain.model.LicenseCategory
import com.dereva.smart.ui.screens.auth.AuthViewModel
import com.dereva.smart.ui.screens.auth.CategorySelectionScreen
import com.dereva.smart.ui.screens.auth.LoginScreen
import com.dereva.smart.ui.screens.auth.RegisterScreen
import com.dereva.smart.ui.screens.auth.VerificationScreen
import com.dereva.smart.ui.screens.auth.ForgotPasswordScreen
import com.dereva.smart.ui.screens.content.ContentViewModel
import com.dereva.smart.ui.screens.content.LessonListScreen
import com.dereva.smart.ui.screens.content.LessonViewerScreen
import com.dereva.smart.ui.screens.content.ModuleListScreen
import com.dereva.smart.ui.screens.home.HomeScreen
import com.dereva.smart.ui.screens.mocktest.MockTestScreen
import com.dereva.smart.ui.screens.mocktest.MockTestViewModel
import com.dereva.smart.ui.screens.payment.PaymentScreen
import com.dereva.smart.ui.screens.payment.PaymentViewModel
import com.dereva.smart.ui.screens.progress.ProgressScreen
import com.dereva.smart.ui.screens.school.SchoolScreen
import com.dereva.smart.ui.screens.simulation.SimulationScreen
import com.dereva.smart.ui.screens.tutor.TutorScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed class Screen(val route: String) {
    object CategorySelection : Screen("category_selection")
    object Login : Screen("login")
    object Register : Screen("register")
    object Verification : Screen("verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "verification/$phoneNumber"
    }
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object MockTest : Screen("mock_test")
    object Progress : Screen("progress")
    object Tutor : Screen("tutor")
    object School : Screen("school")
    object Payment : Screen("payment")
    object ModuleList : Screen("module_list")
    object LessonList : Screen("lesson_list/{moduleId}") {
        fun createRoute(moduleId: String) = "lesson_list/$moduleId"
    }
    object LessonViewer : Screen("lesson_viewer/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson_viewer/$lessonId"
    }
    object Simulation : Screen("simulation")
}

@Composable
fun DerevaNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    // Determine start destination based on user state
    val startDestination = when {
        currentUser == null -> Screen.CategorySelection.route
        currentUser.isGuestMode && currentUser.targetCategory == LicenseCategory.B1 -> Screen.CategorySelection.route
        else -> Screen.Home.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Category Selection Screen
        composable(Screen.CategorySelection.route) {
            CategorySelectionScreen(
                onCategorySelected = { category ->
                    authViewModel.updateGuestCategory(category)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CategorySelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVerification = {
                    val phoneNumber = authViewModel.uiState.value.phoneNumber
                    navController.navigate(Screen.Verification.createRoute(phoneNumber))
                }
            )
        }
        
        composable(
            route = Screen.Verification.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            VerificationScreen(
                viewModel = authViewModel,
                phoneNumber = phoneNumber,
                onNavigateBack = { navController.popBackStack() },
                onVerificationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main app screens
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.MockTest.route) {
            val mockTestViewModel: MockTestViewModel = koinViewModel()
            val authState by authViewModel.uiState.collectAsState()
            
            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    mockTestViewModel.setUserCategory(user.targetCategory.name)
                }
            }
            
            MockTestScreen(
                navController = navController,
                viewModel = mockTestViewModel
            )
        }
        
        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }
        
        composable(Screen.Tutor.route) {
            TutorScreen(navController = navController)
        }
        
        composable(Screen.School.route) {
            SchoolScreen(navController = navController)
        }
        
        composable(Screen.Payment.route) {
            val viewModel: PaymentViewModel = koinViewModel { parametersOf("user123") }
            PaymentScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ModuleList.route) {
            val viewModel: ContentViewModel = koinViewModel { parametersOf("user123") }
            val authState by authViewModel.uiState.collectAsState()
            
            LaunchedEffect(authState.currentUser) {
                viewModel.setCurrentUser(authState.currentUser)
            }
            
            ModuleListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(
            route = Screen.LessonList.route,
            arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val viewModel: ContentViewModel = koinViewModel { parametersOf("user123") }
            val authState by authViewModel.uiState.collectAsState()
            
            LaunchedEffect(authState.currentUser) {
                viewModel.setCurrentUser(authState.currentUser)
            }
            
            LessonListScreen(
                navController = navController,
                viewModel = viewModel,
                moduleId = moduleId
            )
        }
        
        composable(
            route = Screen.LessonViewer.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val viewModel: ContentViewModel = koinViewModel { parametersOf("user123") }
            val authState by authViewModel.uiState.collectAsState()
            
            LaunchedEffect(authState.currentUser) {
                viewModel.setCurrentUser(authState.currentUser)
            }
            
            LessonViewerScreen(
                navController = navController,
                viewModel = viewModel,
                lessonId = lessonId
            )
        }
    }
}
