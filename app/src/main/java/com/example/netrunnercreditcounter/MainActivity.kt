package com.example.netrunnercreditcounter

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.booleanResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.netrunnercreditcounter.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    private var creditsscoretop = 5
    private var creditsscorebottom = 5
    private val handler = Handler(Looper.getMainLooper())

    private var accumulatedChangeTop = 0
    private var accumulatedChangeBottom = 0

    private var isDarkMode = true
    private var isMenuShowing = false

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

        setupButtons()
        setupOverlayLogic()
    }

    private fun setupButtons() {
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
            toggleMenus(!isMenuShowing)
        }
    }

    private fun setupOverlayLogic() {
        // Runner Faction Klicks
        binding.iconAnarch.setOnClickListener {
            setRunnerColor(getColor(R.color.anarch_orange))
            binding.runnerSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}
        binding.iconCriminal.setOnClickListener {
            setRunnerColor(getColor(R.color.criminal_blue))
            binding.runnerSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}
        binding.iconShaper.setOnClickListener {
            setRunnerColor(getColor(R.color.shaper_green))
            binding.runnerSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}

        // Corp Faction Klicks
        binding.iconHB.setOnClickListener {
            setCorpColor(getColor(R.color.hb_lilac))
            binding.corpSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}
        binding.iconJinteki.setOnClickListener {
            setCorpColor(getColor(R.color.jinteki_red))
            binding.corpSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}
        binding.iconNBN.setOnClickListener {
            setCorpColor(getColor(R.color.nbn_yellow))
            binding.corpSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}
        binding.iconWeyland.setOnClickListener {
            setCorpColor(getColor(R.color.weyland_green))
            binding.corpSide.visibility = View.VISIBLE
            binding.blacklayout.setBackgroundColor(android.R.color.transparent)}

        // Settings Buttons
        binding.btnReset.setOnClickListener {
            resetScores()
            toggleMenus(false)
        }
        binding.btnToggleTheme.setOnClickListener {

            isDarkMode = !isDarkMode

            recolorIcons()
            toggleMenus(false)
        }

        binding.btndark.setOnClickListener {
            isDarkMode = true
            applyTheme()
            binding.corpSide.visibility = View.INVISIBLE
            binding.runnerSide.visibility = View.INVISIBLE
            toggleMenus(show = false)
        }

        binding.btnlight.setOnClickListener {
            isDarkMode = false
            applyTheme()
            binding.corpSide.visibility = View.INVISIBLE
            binding.runnerSide.visibility = View.INVISIBLE
            toggleMenus(show = false)
        }

        binding.btnBack.setOnClickListener {
            toggleMenus(false)
        }
    }

    private fun setRunnerColor(color: Int) {
        binding.runnerSide.setBackgroundColor(color)
        toggleMenus(false)
    }

    private fun setCorpColor(color: Int) {
        binding.corpSide.setBackgroundColor(color)
        toggleMenus(false)
    }

    private fun resetScores() {
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

    private fun toggleMenus(show: Boolean) {
        isMenuShowing = show
        val alpha = if (show) 1f else 0f

        if (show) {
            binding.runnerColorOverlay.visibility = View.VISIBLE
            binding.corpColorOverlay.visibility = View.VISIBLE
            binding.settingsOverlay.visibility = View.VISIBLE
            // Start-Position für den Slide-Effekt
            binding.runnerColorOverlay.translationY = -200f
            binding.corpColorOverlay.translationY = 200f
        }

        binding.runnerColorOverlay.animate().alpha(alpha).translationY(0f).setDuration(300)
            .withEndAction { if (!show) binding.runnerColorOverlay.visibility = View.GONE }
        binding.corpColorOverlay.animate().alpha(alpha).translationY(0f).setDuration(300)
            .withEndAction { if (!show) binding.corpColorOverlay.visibility = View.GONE }
        binding.settingsOverlay.animate().alpha(alpha).setDuration(300)
            .withEndAction { if (!show) binding.settingsOverlay.visibility = View.GONE }
    }

    private fun updateChangetop(delta: Int) {
        accumulatedChangeTop += delta
        if (accumulatedChangeTop == 0) {
            binding.changetop.visibility = View.INVISIBLE
        } else {
            val sign = if (accumulatedChangeTop > 0) "+" else ""
            binding.changetop.text = "$sign$accumulatedChangeTop"
            binding.changetop.visibility = View.VISIBLE
            handler.removeCallbacks(hideChangeTop)
            handler.postDelayed(hideChangeTop, 2000)
        }
    }

    private fun updatechangebottom(delta: Int) {
        accumulatedChangeBottom += delta
        if (accumulatedChangeBottom == 0) {
            binding.changebottom.visibility = View.INVISIBLE
        } else {
            val sign = if (accumulatedChangeBottom > 0) "+" else ""
            binding.changebottom.text = "$sign$accumulatedChangeBottom"
            binding.changebottom.visibility = View.VISIBLE
            handler.removeCallbacks(hideChangeBottom)
            handler.postDelayed(hideChangeBottom, 2000)
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



    private fun applyTheme() {
        val bgColor = if (isDarkMode) Color.BLACK else Color.WHITE
        val fgColor = if (isDarkMode) Color.WHITE else Color.BLACK

        binding.blacklayout.setBackgroundColor(bgColor)

        listOf(binding.creditstop, binding.creditsbottom, binding.changetop, binding.changebottom).forEach {
            it.setTextColor(fgColor)
        }
        listOf(binding.plustop, binding.minustop, binding.plusbottom, binding.minusbottom).forEach {
            it.setTextColor(fgColor)
        }
        binding.icon.setColorFilter(fgColor)

        val controller = WindowCompat.getInsetsController(window, binding.root)
        controller.isAppearanceLightStatusBars = !isDarkMode
        controller.isAppearanceLightNavigationBars = !isDarkMode
    }

    private fun recolorIcons()  {

        val fgColor = if (isDarkMode) Color.WHITE else Color.BLACK

        listOf(binding.creditstop, binding.creditsbottom, binding.changetop, binding.changebottom,
            binding.plustop, binding.minustop, binding.plusbottom, binding.minusbottom).forEach {
            it.setTextColor(fgColor)
        }
        binding.icon.setColorFilter(fgColor)

    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showCreditAnimation(anchorView: View, isTop: Boolean) {
        val iconSize = (18 * resources.displayMetrics.density).toInt()
        val startOffset = (155 * resources.displayMetrics.density)
        val travelDist = (-155 * resources.displayMetrics.density)
        val rngValueAnimation = kotlin.random.Random.nextInt(-iconSize, iconSize) / 2

        val creditView = ImageView(this).apply {
            setImageResource(R.drawable.credit)
            setColorFilter(if (isDarkMode) Color.WHITE else Color.BLACK)
            layoutParams = LayoutParams(iconSize, iconSize)
            if (isTop) rotation = 180f
        }
        binding.rootlayout.addView(creditView)

        val location = IntArray(2)
        anchorView.getLocationInWindow(location)
        val rootLocation = IntArray(2)
        binding.rootlayout.getLocationInWindow(rootLocation)

        creditView.x = (location[0] - rootLocation[0] + (anchorView.width - iconSize) / 2 + rngValueAnimation).toFloat()
        creditView.y = if (isTop) (location[1] - rootLocation[1] + anchorView.height + startOffset) else (location[1] - rootLocation[1] - iconSize - startOffset)

        val translationY = if (isTop) travelDist else -travelDist
        creditView.animate().translationYBy(translationY).alpha(1f).scaleX(2.25f).scaleY(2.25f).setDuration(400)
            .withEndAction { binding.rootlayout.removeView(creditView) }.start()
    }

    private fun showCreditDeclineAnimation(anchorView: View, isTop: Boolean) {
        val iconSize = (40 * resources.displayMetrics.density).toInt()
        val rngValueAnimation = kotlin.random.Random.nextInt(-iconSize, iconSize) / 2
        val travelDist = (175 * resources.displayMetrics.density)

        val creditView = ImageView(this).apply {
            setImageResource(R.drawable.credit)
            setColorFilter(if (isDarkMode) Color.WHITE else Color.BLACK)
            layoutParams = LayoutParams(iconSize, iconSize)
            if (isTop) rotation = 180f
        }
        binding.rootlayout.addView(creditView)

        val location = IntArray(2)
        anchorView.getLocationInWindow(location)
        val rootLocation = IntArray(2)
        binding.rootlayout.getLocationInWindow(rootLocation)

        creditView.x = (location[0] - rootLocation[0] + (anchorView.width - iconSize) / 2 + rngValueAnimation).toFloat()
        creditView.y = if (isTop) (location[1] - rootLocation[1] + anchorView.height).toFloat() else (location[1] - rootLocation[1] - iconSize).toFloat()

        val translationY = if (isTop) travelDist else -travelDist
        creditView.animate().translationYBy(translationY).alpha(0f).scaleX(0.25f).scaleY(0.25f).setDuration(400)
            .withEndAction { binding.rootlayout.removeView(creditView) }.start()
    }
}