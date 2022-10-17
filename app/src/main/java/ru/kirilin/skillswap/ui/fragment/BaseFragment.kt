package ru.kirilin.skillswap.ui.fragment

import android.R
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import retrofit2.HttpException

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

	val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
		throwable.printStackTrace()
		activity?.runOnUiThread{showToast(throwable.message ?: "Unknown Error")}

	}

	suspend fun <T: Any> handleRequest(requestFunc: suspend () -> T) {
		withContext(Dispatchers.IO){
			kotlin.runCatching { requestFunc.invoke() }
				.onFailure { er ->
					er.printStackTrace()
					showToast(er.message ?: "Unknown Error")
				}
		}
	}

	fun showToast(
		message: String,
		context: Context = activity?.applicationContext!!,
		duration: Int = Toast.LENGTH_LONG) {
		Toast.makeText(context, message, duration).show()
	}

	fun <T> setSpinnerAdapter(view: View, spinnerId: Int, objects: List<T>) =
		view.findViewById<Spinner?>(spinnerId)
			.also {
				it.adapter = ArrayAdapter(
					this.requireContext(),
					R.layout.simple_spinner_item,
					objects
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
				}
			}
}

const val apiKey = "753009e5-1ad5-44dc-9cc0-ae43b4c6f8ce"