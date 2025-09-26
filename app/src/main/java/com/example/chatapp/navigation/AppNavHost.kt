package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.chatapp.ui.theme.screens.home.HomeScreen
import com.example.chatapp.ui.theme.screens.login.LoginScreen
import com.example.chatapp.ui.theme.screens.signup.SignupScreen
import com.example.chatapp.ui.theme.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(LOGIN_URL) {
            LoginScreen(navController = navController)
        }
        composable(SIGNUP_URL) {
            SignupScreen(navController = navController)
        }
        composable(SPLASH_URL) {
            SplashScreen(navController = navController)
        }
        composable(HOME_URL) {
            HomeScreen(navController = navController)
        }

    }
}