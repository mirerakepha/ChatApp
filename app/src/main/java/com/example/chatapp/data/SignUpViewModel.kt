package com.example.chatapp.data

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val state = _state.asStateFlow()

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun signUp(name: String, email: String, password: String) {
        _state.value = SignUpState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener {
                                _state.value = SignUpState.Success
                            }
                            .addOnFailureListener {
                                _state.value = SignUpState.Error
                            }
                    } else {
                        _state.value = SignUpState.Error
                    }
                } else {
                    _state.value = SignUpState.Error
                }
            }
            .addOnFailureListener {
                _state.value = SignUpState.Error
            }
    }
}

sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    object Error : SignUpState()
}
