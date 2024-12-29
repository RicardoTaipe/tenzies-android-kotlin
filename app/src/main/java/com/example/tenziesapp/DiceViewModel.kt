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
    private val diceGenerator: DiceGenerator,
) : ViewModel() {

    private var diceList: List<Dice> = diceGenerator.generateNewDice()

    private val _diceUi = MutableLiveData(diceList)
    val diceUi: LiveData<List<Dice>> = _diceUi

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    private val _soundEvent = MutableLiveData<Event<Int>>()
    val soundEvent: LiveData<Event<Int>> = _soundEvent

    private val _onGameFinished = MutableLiveData<Event<Unit>>()
    val onGameFinished: LiveData<Event<Unit>> = _onGameFinished


    fun rollDice() {
        if (isGameOverCondition()) {
            restartGame()
        } else {
            updateDiceAfterRoll()
            checkGameOver()
        }
        playSoundIfDiceAreNotHeld()
    }

    fun holdDice(id: String) {
        if (!isGameOverCondition()) {
            toggleDiceSelection(id)
            checkGameOver()
        }
    }

    private fun toggleDiceSelection(id: String) {
        updateDice {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }


    private fun updateDiceAfterRoll() {
        updateDice { dice ->
            if (dice.isSelected) dice else diceGenerator.generateSingleDice()
        }
    }

    private fun updateDice(updateFn: (Dice) -> Dice) {
        diceList = diceList.map(updateFn)
        _diceUi.value = diceList
    }

    private fun playSoundIfDiceAreNotHeld() {
        if (!areAllDiceHeld()) playSound(R.raw.rollingdice)
    }

    private fun isGameOverCondition() = allDiceHaveSameValue() && areAllDiceHeld()

    private fun generateNewDice() = List(10) { Dice() }

    private fun restartGame() {
        generateNewDice().apply {
            diceList = this
            _diceUi.value = this
        }
        _isGameOver.value = false
    }

    private fun playSound(@RawRes rawRes: Int) {
        _soundEvent.value = Event(rawRes)
    }

    private fun checkGameOver() {
        if (isGameOverCondition()) {
            _isGameOver.value = true
            playSound(R.raw.goodresult)
            _onGameFinished.value = Event(Unit)
        }
    }

    private fun areAllDiceHeld() = diceList.all { it.isSelected }
    private fun allDiceHaveSameValue(): Boolean = diceList.run {
        val firstValue = first().value
        all { it.value == firstValue }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenziesApplication)
                val diceGenerator = application.diceGenerator
                DiceViewModel(diceGenerator)
            }
        }
    }
}
