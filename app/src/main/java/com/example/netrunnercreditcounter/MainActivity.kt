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
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
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



//Setup of all the buttons that are using the helpfunction
        setupAutoRepeat(binding.plustop) {
            creditsscoretop++
            binding.creditstop.text = creditsscoretop.toString()
            updateChangetop(1)
            showCreditAnimation(binding.plustop, true)
        }
        setupAutoRepeat(binding.minustop) {
            if (creditsscoretop > 0) {
                creditsscoretop--
                binding.creditstop.text = creditsscoretop.toString()
                updateChangetop(-1)
                showCreditDeclineAnimation(binding.minustop, true)
            }
        }

        setupAutoRepeat(binding.plusbottom) {
            creditsscorebottom++
            binding.creditsbottom.text = creditsscorebottom.toString()
            updatechangebottom(1)
            showCreditAnimation(binding.plusbottom, false)

        }

        setupAutoRepeat(binding.minusbottom) {
            if (creditsscorebottom > 0) {
                creditsscorebottom--
                binding.creditsbottom.text = creditsscorebottom.toString()
                updatechangebottom(-1)
                showCreditDeclineAnimation(binding.minusbottom, false)
            }
        }
        binding.icon.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            android.app.AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle("Settings")
                .setMessage("")
                .setPositiveButton("Reset to 5") { _, _ ->
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
                .setNegativeButton("Back", null)
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
                handler.postDelayed(this, 250)
            }
        }
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.removeCallbacks(runnable)
                    action()
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    handler.postDelayed(runnable, 300)
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
        //Configure the bg of the app
        binding.blacklayout.setBackgroundColor(bgcolor)

        //Configure the textcolor
        val textViews   = listOf(
            binding.creditstop, binding.creditsbottom,
            binding.changetop, binding.changebottom
        )
        textViews.forEach { it.setTextColor(fgColor)}

        // Configure the colorscheme of the buttons - here the bgcolor needs to be changed as well
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

    private fun showCreditAnimation(anchorView: View, isTop: Boolean) {
        val iconSize    = (24 * resources.displayMetrics.density).toInt()
        val startOffset = (100 * resources.displayMetrics.density) // der offset wird in Wahrheit nicht gebraucht, da nun die Startpunkte an den Kanten des Symbols sind, aber will es dennoch drinnen lassen
        val travelDist  = (-100 * resources.displayMetrics.density)
        val rngValueAnimation = kotlin.random.Random.nextInt(-iconSize, iconSize) / 2

    val creditView = ImageView(this).apply {
        setImageResource(R.drawable.credit)
        setColorFilter(if (isDarkMode) Color.WHITE else Color.BLACK)
        layoutParams = LayoutParams(
            iconSize,
            iconSize
        ) //Size, Size daher, weil wir davor die Größe schon oben berechnet haben und es in dem Fall ne Variable war
        if (isTop) rotation = 180f
    }
    //Zum Hauptlayout dazugeben
    binding.rootlayout.addView(creditView)

    // Calculate Positions
////Find den Code hier ein bisschen ugh
    val location = IntArray(2)
    anchorView.getLocationInWindow(location)
    val rootLocation = IntArray(2)
    binding.rootlayout.getLocationInWindow(rootLocation)
    val buttonTop = location[1] - rootLocation[1]
    val buttonBottom = buttonTop + anchorView.height

    val x = location[0] - rootLocation[0] + (anchorView.width - iconSize) / 2 + rngValueAnimation
    val y = if (isTop) {
        buttonBottom + startOffset
    } else {
        buttonTop - iconSize - startOffset
    }

////Find den Code hier ein bisschen ugh
        creditView.x = x.toFloat()
        creditView.y = y.toFloat()


     val translationY = if (isTop) travelDist else -travelDist

     creditView.animate()
         .translationYBy(translationY)
         .alpha(0f)
         .scaleX(2.5f)
         .scaleY(2.5f)
         .setDuration(800)
         .withEndAction {
             binding.rootlayout.removeView(creditView)
         }
         .start()


    }

    private fun showCreditDeclineAnimation(anchorView: View, isTop: Boolean) {
        val iconSize = (24 * resources.displayMetrics.density).toInt()
        val rngValueAnimation = kotlin.random.Random.nextInt(-iconSize, iconSize) / 2
        val travelDist  = (100 * resources.displayMetrics.density)
        val dropOffset = (200 * resources.displayMetrics.density)

        val creditView = ImageView(this).apply {
            setImageResource(R.drawable.credit)
            setColorFilter(if (isDarkMode) Color.WHITE else Color.BLACK)
            layoutParams = LayoutParams(
                iconSize,
                iconSize)
            if (isTop) rotation = 180f
            }

        binding.rootlayout.addView(creditView)

        val location = IntArray(2)
        anchorView.getLocationInWindow(location)
        val rootLocation = IntArray(2)
        binding.rootlayout.getLocationInWindow(rootLocation)
        val buttonMinusTop = location[1] - rootLocation[1]
        val buttonMinusBottom = buttonMinusTop + anchorView.height


        val x = location[0] - rootLocation[0] + (anchorView.width - iconSize) / 2 + rngValueAnimation
        val y = if (isTop) {
            buttonMinusBottom
        } else {
            buttonMinusTop - iconSize
        }

        creditView.x = x.toFloat()
        creditView.y = y.toFloat()


        val translationY = if (isTop) travelDist else -travelDist

        creditView.animate()
            .translationYBy(translationY)
            .alpha(0f)
            .scaleX(0.25f)
            .scaleY(0.25f)
            .setDuration(800)
            .withEndAction {
                binding.rootlayout.removeView(creditView)
            }
            .start()
        }
    }
