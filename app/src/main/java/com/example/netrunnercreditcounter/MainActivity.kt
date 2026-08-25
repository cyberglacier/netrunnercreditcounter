package com.example.netrunnercreditcounter
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import com.example.netrunnercreditcounter.databinding.ActivityMainBinding
import android.os.Handler
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    private var creditsscoretop = 5
    private var creditsscorebottom = 5
    private val handler = Handler(Looper.getMainLooper())

    private var accumulatedChangeTop = 0
    private var accumulatedChangeBottom = 0

    private var isDarkMode = true

    private val hideChangeTop = Runnable {
        binding.changetop.visibility = View.INVISIBLE
        accumulatedChangeTop = 0
    }
    private val hideChangeBottom = Runnable {
        binding.changebottom.visibility = View.INVISIBLE
        accumulatedChangeBottom = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()


//Setup für alle Buttons mit der neuen Hilfsfunktion
        setupAutoRepeat(binding.plustop) {
            creditsscoretop++
            binding.creditstop.text = creditsscoretop.toString()
            updateChangetop(1)
        }
        setupAutoRepeat(binding.minustop) {
            if (creditsscoretop > 0) {
                creditsscoretop--
                binding.creditstop.text = creditsscoretop.toString()
                updateChangetop(-1)
            }
        }

        setupAutoRepeat(binding.plusbottom) {
            creditsscorebottom++
            binding.creditsbottom.text = creditsscorebottom.toString()
            updatechangebottom(1)
        }

        setupAutoRepeat(binding.minusbottom) {
            if (creditsscorebottom > 0) {
                creditsscorebottom--
                binding.creditsbottom.text = creditsscorebottom.toString()
                updatechangebottom(-1)
            }
        }
        binding.icon.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            android.app.AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle("Einstellungen")
                .setMessage("Was möchtest du tun?")
                .setPositiveButton("Reset auf 5") { _, _ ->
                    creditsscoretop = 5
                    creditsscorebottom = 5

                    binding.creditstop.text = "5"
                    binding.creditsbottom.text = "5"

                    accumulatedChangeTop = 0
                    accumulatedChangeBottom = 0
                    binding.changetop.visibility = View.INVISIBLE
                    binding.changebottom.visibility = View.INVISIBLE

                    handler.removeCallbacks(hideChangeTop)
                    handler.removeCallbacks(hideChangeBottom)
                }
                .setNeutralButton(if (isDarkMode) "Light Mode" else "Dark Mode") {_,_ ->
                    toggleTheme()
                }
                .setNegativeButton("Zurück", null)
                .show()
        }
    }

    private fun updateChangetop(delta: Int){
        accumulatedChangeTop += delta
        if (accumulatedChangeTop == 0) {
            binding.changetop.visibility = View.INVISIBLE
        } else {
            val sign = if (accumulatedChangeTop > 0) "+" else ""
            binding.changetop.text = "$sign$accumulatedChangeTop"
            binding.changetop.visibility = View.VISIBLE
            handler.removeCallbacks(hideChangeTop)
            handler.postDelayed (hideChangeTop, 2000)
        }
    }

    private fun updatechangebottom(delta: Int){
        accumulatedChangeBottom += delta
        if (accumulatedChangeBottom == 0) {
            binding.changebottom.visibility = View.INVISIBLE
        } else {
            val sign = if (accumulatedChangeBottom > 0) "+" else ""
            binding.changebottom.text = "$sign$accumulatedChangeBottom"
            binding.changebottom.visibility = View.VISIBLE

            handler.removeCallbacks(hideChangeBottom)
            handler.postDelayed (hideChangeBottom, 2000)
        }
    }

    private fun setupAutoRepeat(view: View, action: () -> Unit) {
        val runnable = object : Runnable {
            override fun run() {
                action()
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                handler.postDelayed(this, 100)
            }
        }
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.removeCallbacks(runnable)
                    action()
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    handler.postDelayed(runnable, 500)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(runnable)

                }
            }
            true
        }
    }

    private fun toggleTheme() {
        isDarkMode = !isDarkMode
        applyTheme()
    }

    private fun applyTheme() {
        val bgcolor = if (isDarkMode) Color.BLACK else Color.WHITE
        val fgColor = if (isDarkMode) Color.WHITE else Color.BLACK
        //Hintergrund der App anpassen
        binding.blacklayout.setBackgroundColor(bgcolor)

        //Textfarben
        val textViews   = listOf(
            binding.creditstop, binding.creditsbottom,
            binding.changetop, binding.changebottom
        )
        textViews.forEach { it.setTextColor(fgColor)}

        // Alle Buttons anpassen
        val buttons = listOf(
            binding.plustop, binding.minustop,
            binding.plusbottom, binding.minusbottom
        )
        buttons.forEach {
            it.setTextColor(fgColor)
            it.setBackgroundColor(bgcolor)
        }

        //Zentrales Icon anpassen
        binding.icon.setColorFilter(fgColor)

        // Anpassen der Uhrzeit etc.
        val controller = WindowCompat.getInsetsController(window, binding.root)
        controller.isAppearanceLightStatusBars = !isDarkMode
        controller.isAppearanceLightNavigationBars = !isDarkMode

    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, binding.root).let { controller ->

            controller.hide(WindowInsetsCompat.Type.systemBars())

            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }

    }
}