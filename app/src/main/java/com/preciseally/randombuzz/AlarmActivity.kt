package com.preciseally.randombuzz

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.RingtoneManager
import android.media.Ringtone
import android.os.VibrationEffect
import android.os.Build
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator





class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(500)
        }
    }
}
