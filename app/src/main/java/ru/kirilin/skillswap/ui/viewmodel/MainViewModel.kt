package ru.kirilin.skillswap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kirilin.skillswap.config.launchIO
import ru.kirilin.skillswap.config.withMain
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import java.util.*

class MainViewModel(user: User) : ViewModel() {
    private val _mutableUserState = MutableLiveData(user)

    val registrationState: LiveData<User> get() = _mutableUserState

    fun onRefresh(user: User) {
        viewModelScope.launch(Dispatchers.Main) {

            _mutableUserState.value = user
        }
    }

    fun updateUserLogo(logoId: UUID) =
        viewModelScope.launchIO(
            action = {
                RetrofitModule.userApi.updateUser(
                    _mutableUserState.value?.apply {
                        this.logoId = logoId
                    }!!
                )
                withMain {
                    _mutableUserState.postValue(_mutableUserState.value)
                }
            },
            onError = {}
        )
}