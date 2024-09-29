package com.example.tenziesapp

import androidx.annotation.RawRes
import androidx.lifecycle.*

class DiceViewModel(private val diceGenerator: DiceGenerator) : ViewModel() {
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
        if (isGameOver()) {
            restartGame()
        } else {
            updateDiceAfterRoll()
            checkGameOver()
        }
        playSound(R.raw.rollingdice)
    }

    private fun toggleDiceSelection(id: String) {
        diceList = diceList.map { dice ->
            if (dice.id == id) dice.copy(isSelected = !dice.isSelected) else dice
        }.also { _diceUi.value = it }

    }

    private fun updateDiceAfterRoll() {
        diceList = diceList.map { dice ->
            if (dice.isSelected) dice else diceGenerator.generateNewDice().first()
        }.also { _diceUi.value = it }
    }

    fun holdDice(id: String) {
        if (!isGameOver()) {
            toggleDiceSelection(id)
            checkGameOver()
        }
    }

    private fun restartGame() {
        diceGenerator.generateNewDice().apply {
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

    private fun isGameOver(): Boolean = _isGameOver.value == true
    private fun areAllDiceHeld() = diceList.all { it.isSelected }
    private fun allDiceHaveSameValue(): Boolean = diceList.run {
        val firstValue = first().value
        all { it.value == firstValue }
    }


    companion object {
        fun provideFactory(diceGenerator: DiceGenerator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DiceViewModel::class.java)) {
                        return DiceViewModel(diceGenerator) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
