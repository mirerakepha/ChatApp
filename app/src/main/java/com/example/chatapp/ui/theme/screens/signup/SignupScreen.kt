package com.example.chatapp.ui.theme.screens.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.data.SignUpState
import com.example.chatapp.data.SignUpViewModel
import com.example.chatapp.ui.theme.MarPurple



@Composable
fun SignupScreen(navController: NavController) {
    val viewModel : SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current


    val visualTransformation: VisualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()


    LaunchedEffect(key1 = uiState.value) {
        when (uiState.value){
            is SignUpState.Success -> {
                navController.navigate("home")
            }
            is SignUpState.Error -> {
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
            painter = painterResource(id = R.drawable.chatbubble),
            contentDescription = "logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "username", tint = MarPurple)
            },
            label = { Text("Username") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MarPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "email", tint = MarPurple)
            },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(10.dp),
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password", tint = MarPurple) },
            visualTransformation = visualTransformation,
            trailingIcon = {
                val icon = if (passwordVisible) painterResource(id = R.drawable.pwds)
                else painterResource(id = R.drawable.pwdh)
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        //Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            shape = RoundedCornerShape(10.dp),
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password", tint = MarPurple) },
            visualTransformation = visualTransformation,
            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.value == SignUpState.Loading){
            CircularProgressIndicator()
        }else{
            // Signup Button
            Button(
                onClick = {
                    viewModel.signUp(username, email, password)

                },
                enabled = username.isEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = MarPurple),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text(text = "Signup")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Already have an account? Login",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("login") }
            )


        }



        Spacer(modifier = Modifier.height(20.dp))

        // Divider with OR
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
            Text(" OR ", color = Color.Gray, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google Sign-In
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = MarPurple)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.googlelogo),
                contentDescription = "Google Sign-In",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google", color = Color.White)
        }


    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen(){
    SignupScreen(navController = rememberNavController())
}
