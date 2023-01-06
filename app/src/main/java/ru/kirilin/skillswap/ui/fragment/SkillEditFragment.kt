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
import ru.kirilin.skillswap.data.model.Level
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.Skill
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel
import java.math.BigDecimal


class SkillEditFragment(var skill: Skill? = null) : BaseFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    var title: EditText? = null
    var level: Spinner? = null
    var experience: Spinner? = null
    var price: EditText? = null
    var addBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.skill_edit_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.skill_name)
        level = setSpinnerAdapter(view, R.id.skill_level_spinner, Level.values().asList())
        experience = setSpinnerAdapter(view, R.id.experience_spinner, (1..25).toList())

        price = view.findViewById(R.id.skill_edit_price)
        addBtn = view.findViewById(R.id.add_skill_btn)
        if(skill != null){
            addBtn?.setText(R.string.action_Edit)
        }

        fillWhenEditing()

        addBtn?.setOnClickListener {
            when {
                skill == null -> addSkill()
                else -> editSkill(skill!!)
            }
        }
    }

    private fun fillWhenEditing() {
        if (skill != null) {
            title?.setText(skill?.name)
            level?.setSelection(skill?.level?.ordinal ?: 0)
            experience?.setSelection(skill?.experience ?: 0)
            price?.setText(skill?.price.toString())
        }
    }

    private fun addSkill() {
        require(title?.text.toString().isNotBlank()) { showToast(message = "Fill title field!") }
        viewLifecycleOwner.lifecycleScope.launchIO(
            action = {
                RetrofitModule.skillApi.createNewSkill(
                    Skill(
                        name = title?.text.toString(),
                        level = Level.valueOf(level?.selectedItem.toString()),
                        price = BigDecimal(price?.text.toString()),
                        experience = experience?.selectedItem.toString().toInt()
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

    private fun editSkill(skill: Skill) {
        require(title?.text.toString().isNotBlank()) { showToast(message = "Fill title field!") }
        viewLifecycleOwner.lifecycleScope.launchIO(
            action = {
                RetrofitModule.skillApi.updateSkill(
                    Skill(
                        name = title?.text.toString(),
                        level = Level.valueOf(level?.selectedItem.toString()),
                        price = BigDecimal(price?.text.toString())
                    ),
                    skill.id!!
                )
            },
            onError = {
                showToast(it.message ?: "Unknown Error")
            }
        )
            .invokeOnCompletion { activity?.supportFragmentManager?.popBackStack() }
    }
}