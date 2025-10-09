package com.example.tenziesapp

import androidx.annotation.RawRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class DiceViewModel(
    val gameManager: GameManager,
) : ViewModel() {

    private val _diceUi = MutableLiveData(gameManager.currentDiceState)
    val diceUi: LiveData<List<Dice>> = _diceUi

    private val _isGameOver = MutableLiveData(gameManager.isGameOver)
    val isGameOver: LiveData<Boolean> = _isGameOver

    private val _soundEvent = MutableLiveData<Event<Int>>()
    val soundEvent: LiveData<Event<Int>> = _soundEvent

    private val _onGameFinished = MutableLiveData<Event<Unit>>()
    val onGameFinished: LiveData<Event<Unit>> = _onGameFinished

    init {
        updateUiState()
    }

    /**
     * Centralized logic to pull the latest state from GameManager and update LiveData.
     */
    private fun updateUiState() {
        _diceUi.value = gameManager.currentDiceState
        _isGameOver.value = gameManager.isGameOver
        if (gameManager.isGameOver) {
            playSound(R.raw.goodresult)
            _onGameFinished.value = Event(Unit)
        }
    }

    fun rollDice() {
        gameManager.rollDice()
        updateUiState()
        playSoundIfDiceAreNotLocked()
    }

    fun lockDice(diceId: String) {
        if (_isGameOver.value == true) return
        gameManager.lockDice(diceId)
        updateUiState()
    }


    private fun playSoundIfDiceAreNotLocked() {
        val areAllLocked = gameManager.currentDiceState.all { it.isSelected }
        if (!areAllLocked) {
            playSound(R.raw.rollingdice)
        }
    }

    private fun playSound(@RawRes rawRes: Int) {
        _soundEvent.value = Event(rawRes)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenziesApplication)
                val gameManager = application.gameManager
                DiceViewModel(gameManager)
            }
        }
    }
}
