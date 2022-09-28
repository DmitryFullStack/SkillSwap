package ru.kirilin.skillswap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.data.model.Skill

class SkillAdapter : ListAdapter<Skill, SkillViewHolder>(SkillViewHolder.SkillItemCallback()) {

    var skills = listOf<Skill>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.onBind(skills[position])
    }

    override fun getItemCount() = skills.size
}

class SkillViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val title: TextView = itemView.findViewById(R.id.skill_item_title)
    private val level: TextView = itemView.findViewById(R.id.skill_item_level)
    private val experience: TextView = itemView.findViewById(R.id.skill_item_experience)
    private val price: TextView = itemView.findViewById(R.id.skill_item_price)

    fun onBind(skill: Skill) {
        title.text = skill.name
        level.text = skill.level?.name
        price.text = skill.price.toString()
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