package com.codeleg.neoclock.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codeleg.neoclock.R
import com.codeleg.neoclock.databinding.FragmentAlarmBinding
import com.codeleg.neoclock.utils.DialogHelper
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class FragmentAlarm : Fragment() {

    var _binding: FragmentAlarmBinding? = null
    val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater ,container , false)

       return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fabAddAlarm.setOnClickListener {
            DialogHelper.showTimePicker(requireContext() , onTimeSelected = {h, m -> createNewAlarm(h , m) })
        }


        super.onViewCreated(view, savedInstanceState)
    }

     fun createNewAlarm(hour:Int , minute: Int){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}