package ru.kirilin.skillswap.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel

class MainFragment(val viewModel: MainViewModel) : BaseFragment() {

    lateinit var username: TextView
    lateinit var login: TextView
    lateinit var age: TextView
    lateinit var requirements: Button
    lateinit var skills: Button
    lateinit var search: Button

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception, scope active:${coroutineScope.isActive}", throwable)
        coroutineScope = createCoroutineScope()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = view.findViewById(R.id.username)
        login = view.findViewById(R.id.login)
        age = view.findViewById(R.id.age)
        requirements = view.findViewById(R.id.myRequirements)
        skills = view.findViewById(R.id.mySkills)
        search = view.findViewById(R.id.comradeSearch)

        viewModel.registrationState.observe(viewLifecycleOwner){
            username.text = it.login
            login.text = it.login
            age.text = it.age.toString()
        }

    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}