package ru.kirilin.skillswap.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import java.util.*

class MainViewModel(user: User) : ViewModel() {
    private val _mutableUserState = MutableLiveData(user)

    val registrationState: LiveData<User> get() = _mutableUserState

    fun onLoad(user: User) {
        viewModelScope.launch(Dispatchers.Main) {

            _mutableUserState.value = user
            }
        }

}