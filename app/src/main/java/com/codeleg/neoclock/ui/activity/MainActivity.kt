package com.codeleg.neoclock.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codeleg.neoclock.R
import com.codeleg.neoclock.databinding.ActivityMainBinding
import com.codeleg.neoclock.ui.fragment.FragmentAlarm
import com.codeleg.neoclock.ui.fragment.FragmentSettings

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets to the root so system bars don't overlap content
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Restore selected fragment after configuration change
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentAlarm())
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val selected = when (item.itemId) {
                R.id.nav_iem_alarm -> FragmentAlarm()
                R.id.nav_item_stopwatch -> FragmentAlarm()
                R.id.nav_item_settings -> FragmentSettings()
                else -> FragmentAlarm()
            }
            // simple replace without back stack to keep behavior consistent
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selected)
                .commit()
            true
        }
    }
}