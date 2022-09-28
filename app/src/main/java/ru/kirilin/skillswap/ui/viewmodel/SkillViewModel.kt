package ru.kirilin.skillswap.ui.viewmodel

import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.Skill

class SkillViewModel(val accountId: String) : ViewModel() {
    private val skillLiveData = MutableLiveData(onLoad())

    val skillData: LiveData<List<Skill>> get() = skillLiveData

    fun onLoad() =
        runBlocking (Dispatchers.IO) {
                RetrofitModule.skillApi.getAllMySkills(accountId)
        }
}