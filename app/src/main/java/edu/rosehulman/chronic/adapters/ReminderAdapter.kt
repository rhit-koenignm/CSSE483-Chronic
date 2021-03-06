package edu.rosehulman.chronic.adapters

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.Reminder
import edu.rosehulman.chronic.models.ReminderViewModel
import edu.rosehulman.chronic.ui.ReminderListFragment

class ReminderAdapter(val fragment: ReminderListFragment) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    val model = ViewModelProvider(fragment.requireActivity())[ReminderViewModel::class.java]

    fun addListener(fragmentName: String) {
        model.addListener(fragmentName) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(model.getReminderAt(position))
    }

    override fun getItemCount() = model.size()

    fun addReminder(reminder: Reminder?) {
        model.addReminder(reminder)
        this.notifyDataSetChanged()
    }
    fun removeAt(adapterPosition: Int) {
        model.removeReminderAt(adapterPosition)
        this.notifyDataSetChanged()
    }

    fun disableAlarmAt(adapterPosition: Int) {
        model.disableReminderAt(adapterPosition)
    }


    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView = itemView.findViewById<TextView>(R.id.row_reminder_title_textview)
        var timeTitleView = itemView.findViewById<TextView>(R.id.row_reminder_time_textview)
        var isActiveSwitch = itemView.findViewById<SwitchCompat>(R.id.row_reminder_on_switch)
        var dayTextViews = listOf<TextView>(
            itemView.findViewById<TextView>(R.id.letter_view_0),
            itemView.findViewById<TextView>(R.id.letter_view_1),
            itemView.findViewById<TextView>(R.id.letter_view_2),
            itemView.findViewById<TextView>(R.id.letter_view_3),
            itemView.findViewById<TextView>(R.id.letter_view_4),
            itemView.findViewById<TextView>(R.id.letter_view_5),
            itemView.findViewById<TextView>(R.id.letter_view_6)
        )


        init {
            isActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
                model.updatePos(adapterPosition)
                model.setCurrentReminder(isChecked)
                notifyItemChanged(adapterPosition)
                }


            itemView.setOnClickListener {
                model.updatePos(adapterPosition)
                fragment.findNavController().navigate(R.id.nav_reminder_detail, null)
            }

        }

        fun bind(reminder: Reminder) {
            titleTextView.text = reminder.title
            timeTitleView.text = reminder.getTimeString()
            isActiveSwitch.isSelected = reminder.isActive

            Log.d(Constants.TAG, "dayTextViews size: ${dayTextViews.size}")
            Log.d(Constants.TAG, "daysActive size: ${reminder.daysActive.size}")
            for (i in 0 until dayTextViews.size) {
                val dayBool = reminder.daysActive[i]

                if(dayBool) {
                    dayTextViews[i].setTextColor(itemView.resources.getColor(R.color.plum))
                    dayTextViews[i].setTypeface(dayTextViews[i].typeface, Typeface.BOLD)
                } else{
                    dayTextViews[i].setTextColor(itemView.resources.getColor(R.color.pearl))
                    dayTextViews[i].setTypeface(dayTextViews[i].typeface, Typeface.NORMAL)
                }
            }

        }
    }
}