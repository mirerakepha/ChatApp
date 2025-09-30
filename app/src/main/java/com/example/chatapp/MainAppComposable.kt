import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.screens.chat.ChatScreen
import com.example.chatapp.ui.theme.screens.home.HomeScreen
import com.example.chatapp.ui.theme.screens.login.LoginScreen
import com.example.chatapp.ui.theme.screens.signup.SignupScreen
import com.example.chatapp.ui.theme.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MainApp() {

    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if (currentUser != null) "home" else "login"

        NavHost(navController = navController, startDestination = start) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("signup") {
                SignupScreen(navController = navController)
            }
            composable("splash") {
                SplashScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("chat/{channelId}&{channelName}", arguments = listOf(
                navArgument("channelId") {
                    type = NavType.StringType
                },
                navArgument("channelName") {
                    type = NavType.StringType
                }
            )
            ){
                val channelId = it.arguments?.getString("channelId") ?: ""
                val channelName = it.arguments?.getString("channelName") ?: ""
                ChatScreen(navController, channelId, channelName)

            }


        }
    }


}

