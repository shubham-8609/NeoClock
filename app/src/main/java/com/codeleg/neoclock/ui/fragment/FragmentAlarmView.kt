package com.codeleg.neoclock.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.codeleg.neoclock.NeoClock
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.databinding.FragmentAlarmViewBinding
import com.codeleg.neoclock.ui.viewmodel.AlarmViewModelFactory
import com.codeleg.neoclock.utils.DialogHelper
import com.codeleg.neoclock.viewmodel.AlarmViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class FragmentAlarmView() : BottomSheetDialogFragment() {

    private val alarmID: Int by lazy {
        arguments?.getInt(ARG_ID)?:-1
    }

    var  _binding: FragmentAlarmViewBinding? = null
    val binding get() = _binding!!


    private var currentAlarm: Alarm? = null

    private val alarmVM: AlarmViewModel by lazy {
        val app = requireActivity().application as NeoClock
        ViewModelProvider(
            requireActivity(),
            AlarmViewModelFactory(app.alarmRepo, requireActivity().application)
        )[AlarmViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmViewBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe alarm LiveData
        alarmVM.getAlarmLive(alarmID).observe(viewLifecycleOwner) { alarm ->
            if (alarm != null) {
                currentAlarm = alarm
                bindAlarmData(alarm)
            }
        }
        setupChipListeners()
        binding.tvHour.setOnClickListener {
            askNewTime()
        }
        binding.btnEditTime.setOnClickListener {
            askNewTime()
        }
        binding.switchVibrate.setOnCheckedChangeListener {_ , isChecked ->
            changeVibrate(isChecked)
        }
        binding.btnSave.setOnClickListener {
            saveAlarm()
        }
        binding.btnDelete.setOnClickListener {
            currentAlarm?.let { alarmVM.deleteAlarm(it) }
            dismiss()
        }
    }

    private fun changeVibrate(isChecked:Boolean){
        val updated = currentAlarm?.copy(vibrate = isChecked)
        currentAlarm = updated
    }

    private fun askNewTime() {
        val initH = currentAlarm?.hour ?: 8
        val initM = currentAlarm?.minute ?: 0
        // DialogHelper requires an AppCompatActivity for show(), cast the activity
        DialogHelper.showTimePicker(requireActivity() as AppCompatActivity, initH, initM) { h, m ->
            applyNewTime(h, m)
        }
    }

    private fun setupChipListeners() {
        // When any chip changes, update currentAlarm.repeatDays immediately
        val chips = listOf(
            binding.chipMon to 1,
            binding.chipTue to 2,
            binding.chipWed to 3,
            binding.chipThu to 4,
            binding.chipFri to 5,
            binding.chipSat to 6,
            binding.chipSun to 0
        )

        chips.forEach { (chip, dayIndex) ->
            chip.setOnCheckedChangeListener { _, _ ->
                // update currentAlarm snapshot with selected days on main thread
                currentAlarm = currentAlarm?.copy(repeatDays = getSelectedDaysFromChips())
            }
        }

    }

    private fun getSelectedDaysFromChips(): List<Int> {
        val selected = mutableListOf<Int>()
        if (binding.chipSun.isChecked) selected.add(0)
        if (binding.chipMon.isChecked) selected.add(1)
        if (binding.chipTue.isChecked) selected.add(2)
        if (binding.chipWed.isChecked) selected.add(3)
        if (binding.chipThu.isChecked) selected.add(4)
        if (binding.chipFri.isChecked) selected.add(5)
        if (binding.chipSat.isChecked) selected.add(6)
        return selected
    }

    private fun applyNewTime(hour: Int, minute: Int) {
        val old = currentAlarm ?: return
        val updated = old.copy(hour = hour, minute = minute)
        currentAlarm = updated
        bindAlarmData(updated)

    }



    private fun bindAlarmData(alarm: Alarm) {
        binding.tvHour.text = "%02d:%02d".format(if(alarm.hour>12) alarm.hour-12 else alarm.hour , alarm.minute)
        binding.etLabel.setText(alarm.label)
        binding.switchVibrate.isChecked = alarm.vibrate
        binding.tvAmPm.text = if(alarm.hour>12) "PM" else "AM"

        lifecycleScope.launch {
        binding.chipMon.isChecked = alarm.repeatDays.contains(1)
        binding.chipTue.isChecked = alarm.repeatDays.contains(2)
        binding.chipWed.isChecked = alarm.repeatDays.contains(3)
        binding.chipThu.isChecked = alarm.repeatDays.contains(4)
        binding.chipFri.isChecked = alarm.repeatDays.contains(5)
        binding.chipSat.isChecked = alarm.repeatDays.contains(6)
        binding.chipSun.isChecked = alarm.repeatDays.contains(0)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



private fun saveAlarm() {
    val newLabel = binding.etLabel.text.toString()
    val base = currentAlarm ?: return
    val updated = base.copy(label = newLabel, isEnabled = true)
    alarmVM.updateAlarm(updated)
    dismiss()
}

    companion object{
        private const val ARG_ID = "alarm_id"
        fun newInstance(id: Int): FragmentAlarmView{
            val fragment= FragmentAlarmView()
            val bundle = Bundle()
            bundle.putInt(ARG_ID , id)
            fragment.arguments = bundle
            return fragment
        }
    }
    }