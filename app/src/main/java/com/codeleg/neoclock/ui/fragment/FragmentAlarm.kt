package com.codeleg.neoclock.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.codeleg.neoclock.NeoClock
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.databinding.FragmentAlarmBinding
import com.codeleg.neoclock.ui.adapter.AlarmAdapter
import com.codeleg.neoclock.ui.viewmodel.AlarmViewModelFactory
import com.codeleg.neoclock.utils.DialogHelper
import com.codeleg.neoclock.viewmodel.AlarmViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import com.codeleg.neoclock.alarm.AlarmScheduler

class FragmentAlarm : Fragment() {
    var _binding: FragmentAlarmBinding? = null
    val binding get() = _binding!!

    private val alarmVM: AlarmViewModel by lazy {
        val app = requireActivity().application as NeoClock
        ViewModelProvider(
            requireActivity(),
            AlarmViewModelFactory(app.alarmRepo, requireActivity().application)
        )[AlarmViewModel::class.java]
    }

    private val alarmAdapter by lazy {
        AlarmAdapter(
            onToggle = ::onAlarmItemToggle,
            onItemClick = ::onAlarmItemClick,
            onItemLongClick = ::onAlarmItemLongPress
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater ,container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecycler()
        setupListeners()
        observeData()

    }

    private fun setupListeners() {
        binding.fabAddAlarm.setOnClickListener {
            DialogHelper.showTimePicker(requireContext()){h ,  m  -> createNewAlarm(h , m)}
        }
    }

    private fun observeData(){
        alarmVM.allAlarms.observe(viewLifecycleOwner) { alarms ->
            alarmAdapter.submitList(alarms)
        }
    }

    private fun setupRecycler() {
        binding.rvAlarms.adapter = alarmAdapter
    }

    private var isToggleProcessing = false
    fun onAlarmItemToggle(alarm: Alarm){
        if (isToggleProcessing) return
        isToggleProcessing = true
        alarmVM.updateEnabled(alarm.id, alarm.isEnabled)
        // release after some delay
        binding.root.postDelayed({
            isToggleProcessing = false
        }, 300)
    }

    fun onAlarmItemClick(alarm: Alarm){
        FragmentAlarmView.newInstance(alarm.id)
            .show(parentFragmentManager, "Alarm Details")


    }
    fun onAlarmItemLongPress(alarm: Alarm) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Alarm?")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAlarmWithUndo(alarm)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAlarmWithUndo(alarm: Alarm) {
        alarmVM.deleteAlarm(alarm)

        Snackbar.make(binding.root, "Alarm Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                // Reinsert as a new alarm with a fresh id
                val restored = alarm.copy(id = 0)
                alarmVM.insertAlarm(restored)
                Snackbar.make(binding.root, "Alarm Restored", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }




    fun createNewAlarm(hour:Int , minute: Int){
         // Let AlarmScheduler compute and persist nextTriggerTime
         val newAlarm = Alarm(hour = hour , minute = minute , isEnabled = true)

         // insert and schedule after inserted id is returned via ViewModel
         alarmVM.insertAlarm(newAlarm)
     }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}