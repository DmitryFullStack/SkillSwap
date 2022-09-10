package ru.kirilin.skillswap.ui

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

open class BaseFragment : Fragment() {
	
	var coroutineScope = createCoroutineScope()
	
	override fun onDetach() {
		coroutineScope.cancel("It's time")
		
		super.onDetach()
	}

	companion object{
		fun getBaseUrl(): String = "http://10.0.2.2:9090"
	}

	fun getApiKey(): String = apiKey
	
	fun createCoroutineScope() = CoroutineScope(Job() + Dispatchers.IO)
}

const val apiKey = "753009e5-1ad5-44dc-9cc0-ae43b4c6f8ce"