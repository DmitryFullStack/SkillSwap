package ru.kirilin.skillswap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import ru.kirilin.skillswap.config.launchIO
import ru.kirilin.skillswap.config.withMain
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.Skill

class SkillViewModel(val accountId: String) : ViewModel() {
    private val skillLiveData = MutableLiveData(onLoad())

    val skillData: LiveData<List<Skill>> get() = skillLiveData

    fun onLoad() =
        runBlocking (Dispatchers.IO) {
            loadSkills()
        }

    private suspend fun loadSkills() = RetrofitModule.skillApi.getAllMySkills(accountId)
        .sortedWith(
            compareByDescending<Skill> { it.level }
                .thenByDescending { it.experience }
                .thenBy { it.name })

    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines
     * launched by uiScope by calling viewModelJob.cancel()
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun refresh() =
        uiScope.launchIO(
            action = {
                val newSkills = loadSkills()
                withMain { skillLiveData.value = newSkills }
                     },
            onError = {}
        )
}