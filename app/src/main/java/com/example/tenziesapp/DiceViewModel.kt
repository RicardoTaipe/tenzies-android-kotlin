package com.example.tenziesapp

import androidx.lifecycle.*


class DiceViewModel(private val game: TenziesGame) : ViewModel() {
    private val _diceUi = MutableLiveData<List<Dice>>()
    val diceUi: LiveData<List<Dice>>
        get() = _diceUi

    var gameOver: LiveData<Boolean> = Transformations.map(_diceUi) {
        game.isGameOver()
    }

    init {
        _diceUi.value = game.dice
    }

    fun rollDice() {
        _diceUi.value = game.rollDice()
    }

    fun holdDice(id: String) {
        game.holdDice(id)
        _diceUi.value = game.dice
    }

    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiceViewModel::class.java)) {
                    return DiceViewModel(TenziesGame()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}