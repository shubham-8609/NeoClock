package com.codeleg.neoclock.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codeleg.neoclock.NeoClock
import com.codeleg.neoclock.databinding.ActivityAlarmBinding
import com.codeleg.neoclock.ui.viewmodel.AlarmViewModelFactory
import com.codeleg.neoclock.viewmodel.AlarmViewModel
import java.util.Locale

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmVM: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make activity appear on lock screen and turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            // optionally request dismiss keyguard
            // setShowWhenLocked and setTurnScreenOn are enough for most cases
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as NeoClock
        alarmVM = ViewModelProvider(this, AlarmViewModelFactory(app.alarmRepo, application))[AlarmViewModel::class.java]

        val alarmId = intent?.getIntExtra("alarm_id", -1) ?: -1
        if (alarmId != -1) {
            // Observe alarm and update UI; keep simple for scaffold
            alarmVM.getAlarmLive(alarmId).observe(this) { alarm ->
                binding.tvAlarmLabel.text = alarm?.label ?: "Alarm"
                binding.tvAlarmTime.text = String.format(Locale.US, "%02d:%02d", alarm?.hour ?: 0, alarm?.minute ?: 0)
            }
        }

        binding.btnDismiss.setOnClickListener {
            // Stop the service; actual dismissal/scheduling handled elsewhere
            stopService(android.content.Intent(this, com.codeleg.neoclock.alarm.AlarmRingingService::class.java))
            finish()
        }

        binding.btnSnooze.setOnClickListener {
            // For simplicity, stop service and finish; snooze scheduling should be implemented via VM/service
            stopService(android.content.Intent(this, com.codeleg.neoclock.alarm.AlarmRingingService::class.java))
            finish()
        }
    }
}
