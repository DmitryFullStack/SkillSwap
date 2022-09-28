package ru.kirilin.skillswap.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.ui.adapter.SkillAdapter
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel
import ru.kirilin.skillswap.ui.viewmodel.RegistrationViewModel
import ru.kirilin.skillswap.ui.viewmodel.SkillViewModel
import ru.kirilin.skillswap.ui.viewmodel.SkillViewModelFactory

class MainFragment(
    val mainViewModel: MainViewModel,
    val accountId: String,
    ) : BaseFragment() {

    lateinit var username: TextView
    lateinit var login: TextView
    lateinit var age: TextView
    lateinit var requirements: Button
    lateinit var skills: Button
    lateinit var search: Button
    lateinit var addSkillBtn: Button
    lateinit var addReqBtn: Button

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
        addSkillBtn = view.findViewById(R.id.addSkillBtn)
        addReqBtn = view.findViewById(R.id.addReqBtn)

        val skillAdapter = SkillAdapter()
        val viewModel: SkillViewModel = ViewModelProvider(this, SkillViewModelFactory(accountId))
            .get(SkillViewModel::class.java)
        viewModel.skillData.observe(this.viewLifecycleOwner){
            skillAdapter.skills = viewModel.skillData.value!!
        }

        mainViewModel.registrationState.observe(viewLifecycleOwner){
            username.text = it.login
            login.text = it.login
            age.text = it.age.toString()
        }

        addSkillBtn.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, SkillEditFragment(), SkillEditFragment::class.java.simpleName)
                ?.commit()
        }
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}