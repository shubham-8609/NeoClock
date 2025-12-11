package com.codeleg.neoclock.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codeleg.neoclock.NeoClock
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.databinding.FragmentAlarmViewBinding
import com.codeleg.neoclock.ui.viewmodel.AlarmViewModelFactory
import com.codeleg.neoclock.utils.DialogHelper
import com.codeleg.neoclock.viewmodel.AlarmViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

class FragmentAlarmView() : BottomSheetDialogFragment() {

    private val alarmID: Int by lazy {
        arguments?.getInt(ARG_ID)?:-1
    }
    private var isToggleProcessing = false

    var  _binding: FragmentAlarmViewBinding? = null
    val binding get() = _binding!!



    private var currentAlarm: Alarm? = null

    private val alarmVM: AlarmViewModel by lazy {
        val app = requireActivity().application as NeoClock
        ViewModelProvider(
            requireActivity(),
            AlarmViewModelFactory(app.alarmRepo)
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

        alarmVM.getAlarmById(alarmID){alarm ->
            if(alarm!=null){
                currentAlarm = alarm
                bindAlarmData(alarm)
            }
        }
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
            alarmVM.deleteAlarm(currentAlarm!!)
         dismiss()
        }
    }

    private fun changeVibrate(isChecked:Boolean){
        if (isToggleProcessing) return
        val updated = currentAlarm!!.copy(vibrate = isChecked)
        currentAlarm = updated
        binding.root.postDelayed({
            isToggleProcessing = false
        }, 300)
    }

    private fun askNewTime() {
        val initH = currentAlarm?.hour ?: 8
        val initM = currentAlarm?.minute ?: 0
        // DialogHelper requires an AppCompatActivity for show(), cast the activity
        DialogHelper.showTimePicker(requireActivity() as AppCompatActivity, initH, initM) { h, m ->
            applyNewTime(h, m)
        }
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



private fun saveAlarm() {
    val newLabel = binding.etLabel.text.toString()
    alarmVM.updateAlarm(currentAlarm!!.copy(isEnabled = true , label = newLabel))
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