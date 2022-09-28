package ru.kirilin.skillswap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.kirilin.skillswap.data.model.User

class MainViewModelFactory(val user: User) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(user) as T;
    }
}

class SkillViewModelFactory(val accountId: String) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SkillViewModel(accountId) as T;
    }
}