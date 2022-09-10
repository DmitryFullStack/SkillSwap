package ru.kirilin.skillswap.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import retrofit2.Call
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import retrofit2.Callback
import retrofit2.Response
import ru.kirilin.skillswap.MainActivity
import ru.kirilin.skillswap.ui.viewmodel.RegistrationViewModel

class RegistrationFragment(var viewModel: RegistrationViewModel) : BaseFragment() {

    var username: EditText? = null
    var password: EditText? = null
    var confirmation: EditText? = null
    var login: Button? = null
    var loading: ProgressBar? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception, scope active:${coroutineScope.isActive}", throwable)
        coroutineScope = createCoroutineScope()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.username)
        password = view.findViewById(R.id.password)
        login = view.findViewById(R.id.login)
        loading = view.findViewById(R.id.loading)
        confirmation = view.findViewById(R.id.confirmation)

        view.findViewById<Button>(R.id.login).setOnClickListener {
            registerUser()
        }

        viewModel.registrationState.observe(viewLifecycleOwner) { loginResult ->
            loginResult ?: return@observe
            when (loginResult) {
                is RegistrationViewModel.RegisterResult.InProgress -> login?.isEnabled = false
                is RegistrationViewModel.RegisterResult.Success -> login?.isEnabled = true
                is RegistrationViewModel.RegisterResult.Error -> login?.isEnabled = false
            }
        }

        confirmation?.doOnTextChanged {text, b, c, d ->
            if(username?.text?.isNotBlank() == true &&
                text?.isNotBlank() == true &&
                   password?.text.toString() == text.toString() ){
                viewModel.register(username?.text.toString(), password?.text.toString(), text.toString())
            }
        }
    }

    private fun registerUser() {
            if (username?.text.isNullOrEmpty() ||
                password?.text.isNullOrEmpty() ||
                confirmation?.text.isNullOrEmpty()
            ) {
                Toast.makeText(context, "Fill all fields correctly!", Toast.LENGTH_LONG).show()
                return
            }
            val persistedUser = User(
                username?.text.toString(),
                18, "MALE",
                username?.text.toString()
            )

        try {
            RetrofitModule.userApi.createUser(persistedUser)
        } catch (ex: Throwable) {
            Toast.makeText(context, "Cannot send data to server!", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Cannot send data to server!", ex)
            return
        }
//        call.enqueue(object : Callback<User?> {
//            override fun onResponse(call: Call<User?>, response: Response<User?>) {
//                user = response.body()
//            }
//
//            override fun onFailure(call: Call<User?>, t: Throwable) {
//                Toast.makeText(context, "Cannot send data to server!", Toast.LENGTH_LONG).show()
//                return
//            }
//        })

        val activity = activity as MainActivity
        activity.startMainFragment()
    }


    companion object {
        private val TAG = RegistrationFragment::class.java.simpleName
    }
}