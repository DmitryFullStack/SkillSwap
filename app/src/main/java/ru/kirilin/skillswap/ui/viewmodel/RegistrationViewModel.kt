package ru.kirilin.skillswap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    private val _mutableRegisterState = MutableLiveData<RegisterResult>(RegisterResult.InProgress())

    val registrationState: LiveData<RegisterResult> get() = _mutableRegisterState

    fun register(userName: String, p3assword: String, confirmation: String) {
        viewModelScope.launch(Dispatchers.Main) {

            _mutableRegisterState.value = RegisterResult.Success()
            }
        }

    sealed class RegisterResult {

        class InProgress : RegisterResult()

        class Success : RegisterResult()

        sealed class Error : RegisterResult() {

            class UserName : Error()

            class Password : Error()
        }
    }
}