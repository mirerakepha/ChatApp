package com.example.chatapp.ui.theme.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.R
import com.example.chatapp.ui.theme.MarPurple
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val currentUser = FirebaseAuth.getInstance().currentUser

    // Fade-in animation
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )

        delay(4000)

        // Navigate after splash
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.chatbubble),
            contentDescription = "App Logo",
            colorFilter = ColorFilter.tint(MarPurple),
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer { alpha = alphaAnim.value } // Apply fade
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ChatApp",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MarPurple,
            modifier = Modifier.graphicsLayer { alpha = alphaAnim.value }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Connecting you with your friends",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.graphicsLayer { alpha = alphaAnim.value }
        )
    }
}









@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}