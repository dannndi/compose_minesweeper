package com.dannndi.playground.data

data class Cell(
    val row: Int,
    val col: Int,
    val hasMine: Boolean = false,
    val isOpened: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMines: Int = 0,
)


