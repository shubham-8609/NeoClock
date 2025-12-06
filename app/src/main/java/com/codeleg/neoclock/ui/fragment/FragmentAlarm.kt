package com.codeleg.neoclock.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codeleg.neoclock.R
import com.codeleg.neoclock.databinding.FragmentAlarmBinding

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
            FragmentAddAlarm().show(parentFragmentManager , "Add alarm bottom sheet.")
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}