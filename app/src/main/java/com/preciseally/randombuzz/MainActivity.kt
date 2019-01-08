package com.preciseally.randombuzz

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {
            if (intent != null && intent.extras != null) {
                text_minutes.setText(intent.extras.getString("text_minutes"))
                text_seconds.setText(intent.extras.getString("text_seconds"))
                text_minutes_random.setText(intent.extras.getString("text_minutes_random"))
                text_seconds_random.setText(intent.extras.getString("text_seconds_random"))

                val repeat = intent.extras.getBoolean("check_loop_alarm")
                if (repeat != null && repeat) {
                    runOnUiThread {
                        check_loop_alarm.isChecked = repeat
                        startTimer()
                    }
                }


                val doVibrate = intent.extras.getBoolean("do_vibrate")
                if (doVibrate != null && doVibrate) {
                    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val timings = LongArray(5)
                    timings[0] = 1000L
                    timings[1] = 500L
                    timings[2] = 500L
                    timings[3] = 500L
                    timings[4] = 1000L

                    val amplitudes = IntArray(5)
                    amplitudes[0] = 255
                    amplitudes[1] = 0
                    amplitudes[2] = 225
                    amplitudes[3] = 0
                    amplitudes[4] = 100
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    } else {
                        //deprecated in API 26
                        v.vibrate(2000)
                    }
                }
            }

            btn_start_timer.setOnClickListener {
                startTimer()
            }
            btn_cancel_timer.setOnClickListener {
                cancelTimer()
            }
        }
    }

    private fun cancelTimer() {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        ).cancel()
    }

    private fun getLong(text: String?): Long = when(text) {
        null -> 0L
        "" -> 0L
        else -> Integer.parseInt(text).toLong()
    }

    private fun startTimer()  {
        val minutes = getLong(text_minutes.text.toString())
        val seconds = getLong(text_seconds.text.toString())

        val randomMinutes = getLong(text_minutes_random.text.toString())
        val randomSeconds = getLong(text_seconds_random.text.toString())

        val initialMilliseconds = (minutes * 60 * 1000) + (seconds * 1000)

        val randomMilliseconds = (randomMinutes * 60 * 1000) + (randomSeconds * 1000)

        val milliseconds = if (randomMilliseconds > 0) {
            var rangeMin = listOf(initialMilliseconds, randomMilliseconds).min()

            if (rangeMin == null) {
                rangeMin = 0L
            }
            val randomLong = ThreadLocalRandom.current().nextLong(-rangeMin, randomMilliseconds)

            initialMilliseconds + randomLong
        } else {
             initialMilliseconds
        }

        val alarmTime = System.currentTimeMillis() + milliseconds

        val intent = Intent(this, MainActivity::class.java)

        val checked = check_loop_alarm.isChecked

        intent.putExtra("text_minutes", text_minutes.text.toString())
        intent.putExtra("text_seconds", text_seconds.text.toString())
        intent.putExtra("text_minutes_random", text_minutes_random.text.toString())
        intent.putExtra("text_seconds_random", text_seconds_random.text.toString())

        intent.putExtra("check_loop_alarm", checked)
        intent.putExtra("do_vibrate", true)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

        val timer = object: CountDownTimer(milliseconds, 1000) {
            override  fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val timerSeconds = seconds % 60
                val timerMinutes = seconds / 60

                val txtSeconds = with (timerSeconds.toString()) {
                    ("00" + this).substring(this.length)
                }

                text_countdown.text = if (timerMinutes > 60) {
                    val txtMinutes = with ((minutes % 60).toString()) {
                        ("00" + this).substring(this.length)
                    }

                    "${timerMinutes / 60}:$txtMinutes:$txtSeconds"
                } else {
                    val txtMinutes = with (timerMinutes.toString()) {
                        ("00" + this).substring(this.length)
                    }
                    "$txtMinutes:$txtSeconds"
                }
            }

            override fun onFinish() {
                text_countdown.text = "00:00"
            }

        }

        timer.start()
    }
}
