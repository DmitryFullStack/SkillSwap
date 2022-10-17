package ru.kirilin.skillswap.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.ui.adapter.RequirementAdapter
import ru.kirilin.skillswap.ui.adapter.SkillAdapter
import ru.kirilin.skillswap.ui.viewmodel.*

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

    lateinit var skillViewModel: SkillViewModel
    lateinit var skillAdapter: SkillAdapter

    lateinit var requirementVM: RequirementViewModel
    lateinit var requirementAdapter: RequirementAdapter

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

        val skillRecyclerView = view.findViewById<RecyclerView>(R.id.rv_skills)
        val requirementRecyclerView = view.findViewById<RecyclerView>(R.id.rv_requirements)

        setSkillAdapter(skillRecyclerView)
        setRequirementAdapter(requirementRecyclerView)

        mainViewModel.registrationState.observe(viewLifecycleOwner){
            username.text = it.login
            login.text = it.login
            age.text = it.age.toString()
        }

        addSkillBtn.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, SkillEditFragment(), SkillEditFragment::class.java.simpleName)
                ?.addToBackStack("")
                ?.commit()
        }

        addReqBtn.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, RequirementEditFragment(), RequirementEditFragment::class.java.simpleName)
                ?.addToBackStack("")
                ?.commit()
        }
    }

    override fun onResume() {
        skillViewModel.refresh()
        requirementVM.refresh()
        super.onResume()
    }

    private fun setSkillAdapter(recyclerView: RecyclerView) {
        skillAdapter = SkillAdapter()
        skillViewModel = ViewModelProvider(this, SkillViewModelFactory(accountId))
            .get(SkillViewModel::class.java)
        skillViewModel.skillData.observe(this.viewLifecycleOwner) {
            skillAdapter.submitList(skillViewModel.skillData.value!!)
        }

        recyclerView.layoutManager = LinearLayoutManager(this.context,
            RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = skillAdapter
    }

    private fun setRequirementAdapter(recyclerView: RecyclerView) {
        requirementAdapter = RequirementAdapter()
        requirementVM = ViewModelProvider(this, RequirementViewModelFactory(accountId))
            .get(RequirementViewModel::class.java)
        requirementVM.requirementData.observe(this.viewLifecycleOwner) {
            requirementAdapter.submitList(requirementVM.requirementData.value!!)
        }

        recyclerView.layoutManager = LinearLayoutManager(this.context,
            RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = requirementAdapter
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}