package ru.kirilin.skillswap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.data.model.Skill
import ru.kirilin.skillswap.ui.fragment.SkillEditFragment


class SkillAdapter(val activity: FragmentActivity?) : ListAdapter<Skill, SkillViewHolder>(SkillViewHolder.SkillItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item, activity)
    }
}

class SkillViewHolder(var item: View) : RecyclerView.ViewHolder(item) {
    private val title: TextView = itemView.findViewById(R.id.skill_item_title)
    private val level: TextView = itemView.findViewById(R.id.skill_item_level)
    private val experience: TextView = itemView.findViewById(R.id.skill_item_experience)
    private val price: TextView = itemView.findViewById(R.id.skill_item_price)
    private lateinit var skill: Skill

    fun onBind(skill: Skill, activity: FragmentActivity?) {
        this.skill = skill
        title.text = skill.name
        level.text = skill.level?.name
        experience.text = skill.experience?.toString()
        skill.price.takeIf { it != null }
            .apply {
                price.visibility = View.VISIBLE
                price.text = skill.price.toString()
            }
        item.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, SkillEditFragment(skill), SkillEditFragment::class.java.simpleName)
                ?.addToBackStack("")
                ?.commit()
        }
    }

    internal class SkillItemCallback : DiffUtil.ItemCallback<Skill>() {
        override fun areItemsTheSame(oldItem: Skill, newItem: Skill): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Skill, newItem: Skill): Boolean {
            return oldItem.equals(newItem)
        }
    }
}