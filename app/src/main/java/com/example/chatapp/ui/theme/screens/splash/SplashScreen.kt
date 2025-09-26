package com.example.chatapp.ui.theme.screens.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.chatapp.navigation.LOGIN_URL
import kotlinx.coroutines.delay
import com.example.chatapp.R
import com.example.chatapp.ui.theme.MarPurple


@Composable
fun SplashScreen(navController: NavController, isUserLoggedIn: () -> Boolean = { false }) {

    LaunchedEffect(Unit) {
        delay(4000)
        if (isUserLoggedIn()) {
            navController.navigate(LOGIN_URL) {
                popUpTo(0)
            }
        } else {
            navController.navigate(LOGIN_URL) {
                popUpTo(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.chatbubble),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MarPurple),
            modifier = Modifier.size(100.dp)
        )




    }
}










@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}