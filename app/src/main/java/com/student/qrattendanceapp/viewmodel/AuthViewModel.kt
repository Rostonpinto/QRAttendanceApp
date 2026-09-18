package com.student.qrattendanceapp.viewmodel

import androidx.lifecycle.*
import com.student.qrattendanceapp.data.entities.User
import com.student.qrattendanceapp.data.repository.AppRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    private val _resetResult = MutableLiveData<Boolean>()
    val resetResult: LiveData<Boolean> = _resetResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill all fields"
            return
        }
        viewModelScope.launch {
            val user = repository.login(email, password)
            if (user != null) _loginResult.value = user
            else _errorMessage.value = "Invalid email or password"
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        // Empty check
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill all fields"
            return
        }
        // .edu email check
        if (!email.endsWith(".edu") && !email.contains(".edu.")) {
            _errorMessage.value = "Only .edu email addresses are allowed!"
            return
        }
        // Password length check
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }
        viewModelScope.launch {
            // Duplicate check
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                _errorMessage.value = "This email is already registered!"
                return@launch
            }
            repository.registerUser(
                User(name = name, email = email, password = password, role = role)
            )
            _registerResult.value = true
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }
        if (!email.endsWith(".edu") && !email.contains(".edu.")) {
            _errorMessage.value = "Only .edu email addresses are allowed!"
            return
        }
        if (newPassword.isBlank() || newPassword.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user == null) {
                _errorMessage.value = "No account found with this email!"
                return@launch
            }
            // Update password
            val updatedUser = user.copy(password = newPassword)
            repository.updateUser(updatedUser)
            _resetResult.value = true
        }
    }
}

class AuthViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java))
            @Suppress("UNCHECKED_CAST") return AuthViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel")
    }
}