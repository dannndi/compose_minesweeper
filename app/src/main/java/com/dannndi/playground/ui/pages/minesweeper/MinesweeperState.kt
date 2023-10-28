package com.dannndi.playground.ui.pages.minesweeper

import androidx.compose.runtime.Stable
import com.dannndi.playground.data.Cell

@Stable
data class MinesweeperState(
    val col: Int = 8,
    val row: Int = 12,
    val totalMines: Int = 10,
    val availableFlagCount: Int = 10,
    val cells: List<List<Cell>> = listOf(),
    val gameOver: Boolean = false,
    val win: Boolean = false,
    val timeInSec: Int = 0,
)