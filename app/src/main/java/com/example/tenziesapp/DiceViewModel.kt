package com.example.tenziesapp

import androidx.annotation.RawRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class DiceViewModel(private var diceList: List<Dice> = emptyList()) : ViewModel() {

    private val _diceUi = MutableLiveData(diceList)
    val diceUi: LiveData<List<Dice>> = _diceUi

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    private val _soundEvent = MutableLiveData<Event<Int>>()
    val soundEvent: LiveData<Event<Int>> = _soundEvent

    private val _onGameFinished = MutableLiveData<Event<Unit>>()
    val onGameFinished: LiveData<Event<Unit>> = _onGameFinished


    fun rollDice() {
        if (allDiceHaveSameValue() && areAllDiceHeld()) {
            restartGame()
        } else {
            updateDiceAfterRoll()
            checkGameOver()
        }
        if (!areAllDiceHeld()) playSound(R.raw.rollingdice)
    }

    private fun toggleDiceSelection(id: String) {
        diceList = diceList.map { dice ->
            if (dice.id == id) dice.copy(isSelected = !dice.isSelected) else dice
        }.also { _diceUi.value = it }
    }

    private fun updateDiceAfterRoll() {
        diceList = diceList.map { dice ->
            if (dice.isSelected) dice else Dice()
        }.also { _diceUi.value = it }
    }

    fun holdDice(id: String) {
        if (allDiceHaveSameValue() && areAllDiceHeld()) return
        toggleDiceSelection(id)
        checkGameOver()
    }

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
        val isGameOverCondition = areAllDiceHeld() && allDiceHaveSameValue()
        _isGameOver.value = isGameOverCondition

        if (isGameOverCondition) {
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
                val diceList = application.container.diceGenerator
                DiceViewModel(diceList)
            }
        }
    }
}
