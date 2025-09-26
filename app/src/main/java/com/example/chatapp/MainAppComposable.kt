import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun MainApp() {

    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {

        }
    }


}

