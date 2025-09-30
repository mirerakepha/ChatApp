package com.example.chatapp.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Nothing)
    val state = _state.asStateFlow()

    // Lazily get the FirebaseAuth instance
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun login(email: String, password: String) {
        _state.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Task result might be null; guard safely
                    val user = task.result?.user
                    if (user != null) {
                        _state.value = LoginState.Success
                        return@addOnCompleteListener
                    } else {
                        _state.value = LoginState.Error
                    }
                } else {
                    _state.value = LoginState.Error
                }
            }
            .addOnFailureListener {
                _state.value = LoginState.Error
            }
    }
}

sealed class LoginState {
    object Nothing : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    object Error : LoginState()
}
