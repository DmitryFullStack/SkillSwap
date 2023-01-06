package ru.kirilin.skillswap.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.config.launchIO
import ru.kirilin.skillswap.data.model.Gender
import ru.kirilin.skillswap.data.model.Requirement
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel


class RequirementEditFragment(private val requirement: Requirement? = null) : BaseFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    var name: EditText? = null
    var gender: Spinner? = null
    var experience: Spinner? = null
    var description: EditText? = null
    var addBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.requirement_edit_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = view.findViewById(R.id.req_name)
        gender = setSpinnerAdapter(view, R.id.req_gender_spinner, Gender.values().asList())
        experience = setSpinnerAdapter(view, R.id.req_experience_spinner, (1..25).toList())

        description = view.findViewById(R.id.req_description)
        addBtn = view.findViewById<Button?>(R.id.add_req_btn)
        addBtn?.setOnClickListener {
            when {
                requirement == null -> addRequirement()
                else -> editRequirement(requirement!!)
            }
        }
    }

    private fun addRequirement() {
        require(name?.text.toString().isNotBlank()) { showToast(message = "Fill title field!") }
        viewLifecycleOwner.lifecycleScope.launchIO(
            action = {
                RetrofitModule.requirementApi.createNewRequirement(
                    Requirement(
                        name = name?.text.toString(),
                        description = description?.text.toString(),
                        minExperience = experience?.selectedItem.toString().toInt(),
                        gender = Gender.valueOf(gender?.selectedItem.toString())
                    ),
                    viewModel.registrationState.value?.id?.accountNumber
                )
            },
            onError = {
                showToast(it.message ?: "Unknown Error")
            }
        )
            .invokeOnCompletion { activity?.supportFragmentManager?.popBackStack() }
    }

    private fun editRequirement(requirement: Requirement) {
        require(name?.text.toString().isNotBlank()) { showToast(message = "Fill title field!") }
        viewLifecycleOwner.lifecycleScope.launchIO(
            action = {
                RetrofitModule.requirementApi.updateRequirements(
                    Requirement(
                        name = name?.text.toString(),
                        description = description?.text.toString(),
                        minExperience = experience?.selectedItem.toString().toInt(),
                        gender = Gender.valueOf(gender?.selectedItem.toString())
                    ),
                    requirement.id!!
                )
            },
            onError = {
                showToast(it.message ?: "Unknown Error")
            }
        )
            .invokeOnCompletion { activity?.supportFragmentManager?.popBackStack() }
    }
}