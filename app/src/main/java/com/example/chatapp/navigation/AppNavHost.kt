package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chatapp.ui.theme.screens.chat.ChatScreen
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
        composable("chat/{channelId}", arguments = listOf(
            navArgument("channelId") {
                type = NavType.StringType
            }
        )
        ){
            val channelId = it.arguments?.getString("channelId") ?: ""
            ChatScreen(navController, channelId)

        }

    }
}