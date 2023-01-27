package com.example.tenziesapp
import java.util.*
import kotlin.random.Random

data class Dice(
    val id: String = UUID.randomUUID().toString(),
    val value: Int = Random.nextInt(0, 6),
    var isSelected: Boolean = false,
    var rolling: Boolean = true
)