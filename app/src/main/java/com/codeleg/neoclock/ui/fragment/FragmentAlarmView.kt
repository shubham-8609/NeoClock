package com.codeleg.neoclock.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.codeleg.neoclock.NeoClock
import com.codeleg.neoclock.R
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.databinding.FragmentAlarmViewBinding
import com.codeleg.neoclock.ui.viewmodel.AlarmViewModelFactory
import com.codeleg.neoclock.viewmodel.AlarmViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentAlarmView() : BottomSheetDialogFragment() {

    private val alarmID: Int by lazy {
        arguments?.getInt(ARG_ID)?:-1
    }

    var  _binding: FragmentAlarmViewBinding? = null
    val binding get() = _binding!!

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
                bindAlarmData(alarm)
            }
        }
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

private fun FragmentAlarmView.bindAlarmData(alarm: Alarm) {
    binding.tvHour.text = "%02d:%02d".format(if(alarm.hour>12) alarm.hour-12 else alarm.hour , alarm.minute)
    binding.etLabel.setText(alarm.label)
    binding.switchVibrate.isEnabled = alarm.isEnabled
}
