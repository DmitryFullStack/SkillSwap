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
import ru.kirilin.skillswap.data.model.Requirement
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.Skill

class RequirementViewModel(val accountId: String) : ViewModel() {
    private val requirementLiveData = MutableLiveData(onLoad())

    val requirementData: LiveData<List<Requirement>> get() = requirementLiveData

    fun onLoad() =
        runBlocking (Dispatchers.IO) {
            loadRequirements()
        }

    private suspend fun loadRequirements() = RetrofitModule.requirementApi.getAllMyRequirements(accountId)
        .sortedWith(
            compareByDescending<Requirement> { it.name }
                .thenByDescending { it.minExperience })

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
                val newRequirements = loadRequirements()
                withMain { requirementLiveData.value = newRequirements }
                     },
            onError = {}
        )
}