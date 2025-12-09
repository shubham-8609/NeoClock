package com.codeleg.neoclock.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.databinding.AlarmItemBinding

class AlarmAdapter(
    private val onToggle: (Alarm) -> Unit,
    private val onItemClick: (Alarm) -> Unit,
    private val onItemLongClick: (Alarm) -> Unit = {}
) : ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(DiffCallback()) {

    inner class AlarmViewHolder(val binding: AlarmItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm) = with(binding) {

            tvTime.text = "%02d:%02d".format(if(alarm.hour>12) alarm.hour-12 else alarm.hour , alarm.minute)
            tvLabel.text = alarm.label.ifEmpty { "Alarm" }
            switchEnable.isChecked = alarm.isEnabled
            tvAmPm.text = if (alarm.hour>12) "PM" else "AM"

            // Toggle switch
            switchEnable.setOnCheckedChangeListener { _, isChecked ->
                onToggle(alarm.copy(isEnabled = isChecked))
            }

            // Item click
            root.setOnClickListener { onItemClick(alarm) }

            // Long click
            root.setOnLongClickListener {
                onItemLongClick(alarm)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean =
            oldItem == newItem
    }
}
