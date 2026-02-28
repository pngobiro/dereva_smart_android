package com.dereva.smart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    object Profile : Screen("profile")
    object Help : Screen("help")
    object School : Screen("school")
    object Payment : Screen("payment")
    object ModuleList : Screen("module_list")
    object LessonList : Screen("lesson_list/{moduleId}") {
        fun createRoute(moduleId: String) = "lesson_list/$moduleId"
    }
    object LessonViewer : Screen("lesson_viewer/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson_viewer/$lessonId"
    }
    object QuizTaking : Screen("quiz_taking/{quizId}") {
        fun createRoute(quizId: String) = "quiz_taking/$quizId"
    }
    object Simulation : Screen("simulation")
}

@Composable
fun DerevaNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Category Selection Screen (Guest users and users without active subscription)
        composable(Screen.CategorySelection.route) {
            // Redirect users with ACTIVE subscription to home
            LaunchedEffect(currentUser) {
                val user = currentUser
                if (user != null && !user.isGuestMode && user.isSubscriptionActive) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CategorySelection.route) { inclusive = true }
                    }
                }
            }
            
            // Start guest mode if no user exists
            LaunchedEffect(Unit) {
                if (currentUser == null) {
                    authViewModel.startGuestMode()
                }
            }
            
            CategorySelectionScreen(
                onCategorySelected = { category ->
                    // Update guest category
                    authViewModel.updateGuestCategory(category)
                    // Navigate to home
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
            val user = currentUser
            var showCategoryDialog by remember { mutableStateOf(false) }
            
            // Only show category selection for guest users who haven't chosen a category
            // Registered users already have a category from registration
            LaunchedEffect(user) {
                if (user?.isGuestMode == true && 
                    user.targetCategory == LicenseCategory.B1 && 
                    user.name == "Guest User") {
                    showCategoryDialog = true
                } else {
                    user?.let {
                        mockTestViewModel.setUserCategory(it.targetCategory.name)
                    }
                }
            }
            
            if (showCategoryDialog) {
                CategorySelectionScreen(
                    onCategorySelected = { category ->
                        authViewModel.updateGuestCategory(category)
                        showCategoryDialog = false
                        mockTestViewModel.setUserCategory(category.name)
                    }
                )
            } else if (user?.isSubscriptionActive == true || user?.isGuestMode == true) {
                MockTestScreen(
                    navController = navController,
                    viewModel = mockTestViewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Payment.route)
                }
            }
        }
        
        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            com.dereva.smart.ui.screens.profile.ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.Help.route) {
            com.dereva.smart.ui.screens.help.HelpScreen(navController = navController)
        }
        
        composable(Screen.School.route) {
            SchoolScreen(navController = navController)
        }
        
        composable(Screen.Payment.route) {
            val viewModel: PaymentViewModel = koinViewModel { parametersOf(currentUser?.id ?: "guest") }
            PaymentScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Simulation.route) {
            val user = currentUser
            if (user?.isSubscriptionActive == true) {
                SimulationScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    if (user?.isGuestMode == true) {
                        navController.navigate(Screen.Register.route)
                    } else {
                        navController.navigate(Screen.Payment.route)
                    }
                }
            }
        }
        
        composable(
            route = Screen.QuizTaking.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val quizViewModel: com.dereva.smart.ui.screens.quiz.QuizViewModel = koinViewModel()
            com.dereva.smart.ui.screens.quiz.QuizTakingScreen(
                navController = navController,
                viewModel = quizViewModel,
                authViewModel = authViewModel,
                quizId = quizId
            )
        }
        
        composable(Screen.ModuleList.route) {
            val viewModel: ContentViewModel = koinViewModel { parametersOf(currentUser?.id ?: "guest") }
            var showCategoryDialog by remember { mutableStateOf(false) }
            
            // Ensure guest user exists if no user
            LaunchedEffect(currentUser) {
                if (currentUser == null) {
                    authViewModel.startGuestMode()
                }
            }
            
            // Only show category selection for guest users who haven't chosen a category
            // Registered users already have a category from registration
            LaunchedEffect(currentUser) {
                if (currentUser?.isGuestMode == true && 
                    currentUser.targetCategory == LicenseCategory.B1 && 
                    currentUser.name == "Guest User") {
                    showCategoryDialog = true
                }
            }
            
            // Always set current user when it changes
            LaunchedEffect(currentUser?.id, currentUser?.targetCategory) {
                viewModel.setCurrentUser(currentUser)
            }
            
            if (showCategoryDialog) {
                CategorySelectionScreen(
                    onCategorySelected = { category ->
                        authViewModel.updateGuestCategory(category)
                        showCategoryDialog = false
                    }
                )
            } else {
                ModuleListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        
        composable(
            route = Screen.LessonList.route,
            arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val viewModel: ContentViewModel = koinViewModel { parametersOf(currentUser?.id ?: "guest") }
            val currentUserInScreen = currentUser
            
            // Trigger setCurrentUser whenever authState.currentUser changes
            LaunchedEffect(currentUserInScreen?.id, currentUserInScreen?.targetCategory) {
                viewModel.setCurrentUser(currentUserInScreen)
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
            val viewModel: ContentViewModel = koinViewModel { parametersOf(currentUser?.id ?: "guest") }
            val currentUserInScreen = currentUser
            
            // Trigger setCurrentUser whenever authState.currentUser changes
            LaunchedEffect(currentUserInScreen?.id, currentUserInScreen?.targetCategory) {
                viewModel.setCurrentUser(currentUserInScreen)
            }
            
            LessonViewerScreen(
                navController = navController,
                viewModel = viewModel,
                lessonId = lessonId
            )
        }
    }
}
