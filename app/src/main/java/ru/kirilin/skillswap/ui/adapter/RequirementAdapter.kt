package ru.kirilin.skillswap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.data.model.Requirement
import ru.kirilin.skillswap.data.model.Skill

class RequirementAdapter : ListAdapter<Requirement, RequirementViewHolder>(RequirementViewHolder.RequirementItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_requirement, parent, false)
        return RequirementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }
}

class RequirementViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val title: TextView = itemView.findViewById(R.id.req_item_title)
    private val minExperience: TextView = itemView.findViewById(R.id.req_item_minExperience)

    fun onBind(requirement: Requirement) {
        title.text = requirement.name
        minExperience.text = requirement.minExperience.toString()
    }

    internal class RequirementItemCallback : DiffUtil.ItemCallback<Requirement>() {
        override fun areItemsTheSame(oldItem: Requirement, newItem: Requirement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Requirement, newItem: Requirement): Boolean {
            return oldItem.equals(newItem)
        }
    }
}