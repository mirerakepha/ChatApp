package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatapp.ui.theme.screens.chat.ChatScreen
import com.example.chatapp.ui.theme.screens.home.HomeScreen
import com.example.chatapp.ui.theme.screens.login.LoginScreen
import com.example.chatapp.ui.theme.screens.signup.SignupScreen
import com.example.chatapp.ui.theme.screens.splash.SplashScreen // ← Import the correct SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController) // ← This will now use your animated SplashScreen
        }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable(
            route = "chat/{channelId}&{channelName}",
            arguments = listOf(
                navArgument("channelId") { type = NavType.StringType },
                navArgument("channelName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            val channelName = backStackEntry.arguments?.getString("channelName") ?: ""
            ChatScreen(navController, channelId, channelName)
        }
    }
}