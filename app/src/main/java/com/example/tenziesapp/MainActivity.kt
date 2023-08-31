package com.example.tenziesapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tenziesapp.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DiceViewModel by viewModels { DiceViewModel.Factory }
    private val diceAdapter = DiceAdapter()
    private val party = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 5, TimeUnit.SECONDS).max(1000),
        position = Position.Relative(0.5, 0.3)
    )

    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.run {
            adapter = diceAdapter
            itemAnimator = null
        }

        viewModel.diceUi.observe(this) {
            diceAdapter.submitList(it)
        }

        binding.rollBtn.setOnClickListener {
            viewModel.rollDice()
            playSound(R.raw.rollingdice)
        }

        diceAdapter.itemClickListener = { dice, position ->
            if (viewModel.gameOver.value == false) {
                viewModel.holdDice(dice.id)
                diceAdapter.notifyItemChanged(position)
            }
        }

        viewModel.gameOver.observe(this) {
            if (it) {
                binding.rollBtn.text = getString(R.string.game_over)
                binding.confetti.start(party)
                playSound(R.raw.goodresult)
            } else {
                binding.rollBtn.text = getString(R.string.roll)
                if (binding.confetti.isActive()) {
                    binding.confetti.stop(party)
                }
            }
        }
    }

    private fun playSound(@RawRes rawResId: Int) {
        val assetFileDescriptor = applicationContext.resources.openRawResourceFd(rawResId) ?: return
        mediaPlayer.run {
            reset()
            setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )
            prepareAsync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.confetti.stopGracefully()
        mediaPlayer.release()
    }
}