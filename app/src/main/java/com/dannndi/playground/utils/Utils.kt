package com.dannndi.playground.utils

import com.dannndi.playground.data.Cell
import com.dannndi.playground.data.Offset

object Utils {
    val offsets = listOf(
        Offset(-1, -1),
        Offset(-1, 0),
        Offset(-1, 1),
        Offset(0, -1),
        Offset(0, 1),
        Offset(1, -1),
        Offset(1, 0),
        Offset(1, 1),
    )


    fun generateCells(col: Int = 0, row: Int = 0, mines: Int = 0): List<Cell> {
        val cells = mutableListOf<Cell>()
        return cells
    }
}