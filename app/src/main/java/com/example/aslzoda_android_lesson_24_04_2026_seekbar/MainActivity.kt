package com.example.aslzoda_android_lesson_24_04_2026_seekbar

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View-larni id orqali topish
        val seekBar = findViewById<SeekBar>(R.id.musicSeekBar)
        val timeText = findViewById<TextView>(R.id.timeText)
        val totalTimeText = findViewById<TextView>(R.id.totalTimeText)
        val btnPlay = findViewById<ImageView>(R.id.btnPlay)
        val btnPrev = findViewById<ImageView>(R.id.btnPrev)
        val btnNext = findViewById<ImageView>(R.id.btnNext)

        // 1. MediaPlayer-ni res/raw/perfect.mp3 bilan yaratish
        mediaPlayer = MediaPlayer.create(this, R.raw.cheri)

        // Musiqa davomiyligini o'rnatish
        mediaPlayer?.let {
            seekBar.max = it.duration
            totalTimeText.text = formatTime(it.duration)
        }

        // 2. Play/Pause tugmasi
        btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                btnPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer?.start()
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                startSeekBarUpdate(seekBar, timeText)
            }
        }

        // 3. Orqaga 5 sekund (-5000ms)
        btnPrev.setOnClickListener {
            mediaPlayer?.let {
                val newPos = it.currentPosition - 5000
                it.seekTo(if (newPos > 0) newPos else 0)
            }
        }

        // 4. Oldinga 5 sekund (+5000ms)
        btnNext.setOnClickListener {
            mediaPlayer?.let {
                val newPos = it.currentPosition + 5000
                it.seekTo(if (newPos < it.duration) newPos else it.duration)
            }
        }

        // 5. SeekBar surish mantiqi
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
                timeText.text = formatTime(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // 6. Musiqa tugaganda avtomatik holatni qaytarish
        mediaPlayer?.setOnCompletionListener {
            btnPlay.setImageResource(android.R.drawable.ic_media_play)
            seekBar.progress = 0
            timeText.text = "00:00"
        }
    }

    private fun startSeekBarUpdate(sb: SeekBar, tv: TextView) {
        runnable = Runnable {
            mediaPlayer?.let {
                sb.progress = it.currentPosition
                tv.text = formatTime(it.currentPosition)
                handler.postDelayed(runnable, 100) // Har 0.1 sekundda yangilash (Aniq!)
            }
        }
        handler.postDelayed(runnable, 100)
    }

    private fun formatTime(ms: Int): String {
        val minutes = (ms / 1000) / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Fonda ishlashi uchun release qilinmaydi, faqat UI yangilash to'xtatiladi
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}