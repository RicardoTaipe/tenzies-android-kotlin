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
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            setOnPreparedListener { start() }
            setOnCompletionListener { reset() }
        }
    }
    private val party: Party by lazy {
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).max(1000),
            position = Position.Relative(0.5, 0.3)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.run {
            adapter = diceAdapter
            itemAnimator = null
        }
    }

    private fun setupObservers() {
        viewModel.diceUi.observe(this) { diceAdapter.submitList(it) }

        viewModel.isGameOver.observe(this) { isGameOver ->
            if (isGameOver) {
                binding.rollBtn.text = getString(R.string.game_over)
            } else {
                binding.rollBtn.text = getString(R.string.roll)
                if (binding.confetti.isActive()) binding.confetti.stop(party)
            }
        }

        viewModel.soundEvent.observe(this, EventObserver { rawRes ->
            playSound(rawRes)
        })

        viewModel.onGameFinished.observe(this, EventObserver {
            binding.confetti.start(party)
        })
    }

    private fun setupListeners() {
        binding.rollBtn.setOnClickListener {
            viewModel.rollDice()
            diceAdapter.isAnimationEnabled = true
        }

        diceAdapter.itemClickListener = { dice, _ ->
            viewModel.holdDice(dice.id)
        }
    }

    private fun playSound(@RawRes rawResId: Int) {
        applicationContext.resources.openRawResourceFd(rawResId)?.let { assetFileDescriptor ->
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
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.confetti.stopGracefully()
        mediaPlayer.release()
    }
}
//compose
//https://github.com/google-developer-training/basic-android-kotlin-compose-training-unscramble/tree/viewmodel
//views
//https://github.com/google-developer-training/android-basics-kotlin-unscramble-app/tree/main
