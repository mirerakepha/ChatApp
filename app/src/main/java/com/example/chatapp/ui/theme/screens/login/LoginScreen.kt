package com.example.chatapp.ui.theme.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.R
import com.example.chatapp.data.LoginState
import com.example.chatapp.data.LoginViewModel
import com.example.chatapp.ui.theme.MarPurple
import com.google.android.material.progressindicator.CircularProgressIndicator


@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val visualTransformation: VisualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    LaunchedEffect(key1 = uiState.value) {
        when (uiState.value) {
            is LoginState.Success -> {
                navController.navigate("home")
            }
            is LoginState.Error -> {
                Toast.makeText(context, "Problem Occurred", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.messenger),
            contentDescription = "logo",
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.background),
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "email",
                    tint = MarPurple
                )
            },
            label = { Text(text = "Enter Username/Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MarPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MarPurple,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(10.dp),
            label = { Text(text = "Enter Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "password",
                    tint = MarPurple
                )
            },
            visualTransformation = visualTransformation,
            trailingIcon = {
                val icon = if (passwordVisible) painterResource(id = R.drawable.pwds)
                else painterResource(id = R.drawable.pwdh)

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MarPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MarPurple,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.value == LoginState.Loading) {
            CircularProgressIndicator()
        } else {
            // Login button
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = email.isNotEmpty() &&
                        password.isNotEmpty() &&
                        (uiState.value == LoginState.Nothing || uiState.value == LoginState.Error),
                colors = ButtonDefaults.buttonColors(containerColor = MarPurple),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Go to Signup
            Text(
                text = "Don't have an account? Sign up",
                fontSize = 15.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("signup") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OR Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MarPurple)
            Text(" OR ", color = MarPurple, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = MarPurple)
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Google Sign-in (to be implemented later)
        OutlinedButton(
            onClick = {
                Toast.makeText(context, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MarPurple,
                contentColor = MarPurple
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.googlelogo),
                contentDescription = "Google Sign-In",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text("Sign in with Google", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}
