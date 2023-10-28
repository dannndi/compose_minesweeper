package com.dannndi.playground.ui.pages.minesweeper

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannndi.playground.data.Cell
import com.dannndi.playground.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class MinesweeperViewModel : ViewModel() {
    // game state
    private val _gameState = MutableStateFlow(MinesweeperState())
    val gameState = _gameState.asStateFlow()

    private val totalRow = _gameState.value.row
    private val totalCol = _gameState.value.col
    private val availableFlagCount = _gameState.value.availableFlagCount

    init {
        initGame()
    }

    private fun initGame() {
        viewModelScope.launch {

            // create cells
            val cells = mutableListOf<MutableList<Cell>>()
            for (row in 0 until totalRow) {
                val rows = mutableListOf<Cell>()
                for (col in 0 until totalCol) {
                    rows.add(Cell(row = row, col = col, isOpened = false))
                }
                cells.add(rows)
            }

            // add mines to cells
            var count = 0
            while (count < _gameState.value.totalMines) {
                val randomRow = Random.nextInt(totalRow)
                val randomCol = Random.nextInt(totalCol)
                val randomCell = cells[randomRow][randomCol]
                if (randomCell.hasMine) continue

                cells[randomRow][randomCol] = randomCell.copy(hasMine = true)
                count++
            }

            // calculate adjacent mines
            for (row in 0 until totalRow) {
                for (col in 0 until totalCol) {
                    val cell = cells[row][col]
                    if (cell.hasMine) continue

                    var adjacentMines = 0
                    for (offset in Utils.offsets) {
                        val nextRow = row + offset.dy
                        val nextCol = col + offset.dx

                        if (isValidCell(nextRow, nextCol) && cells[nextRow][nextCol].hasMine) {
                            adjacentMines++
                        }
                    }

                    cells[row][col] = cell.copy(adjacentMines = adjacentMines)
                }
            }

            _gameState.value = MinesweeperState(
                cells = cells
            )
        }
    }


    private fun isValidCell(row: Int, col: Int): Boolean {
        val totalRow = totalRow;
        val totalCol = totalCol;
        return row in 0 until totalRow && col in 0 until totalCol;
    }


    private fun checkForWin(): Boolean {
        for (row in _gameState.value.cells) {
            for (cell in row) {
                if (!cell.isOpened && !cell.hasMine) {
                    return false
                }
            }
        }

        return true
    }

    private fun openedCells(): MutableList<MutableList<Cell>> {
        val newCells = _gameState.value.cells.map { it.toMutableList() }.toMutableList()

        for (row in 0 until totalRow) {
            for (col in 0 until totalCol) {
                val cell = newCells[row][col]
                newCells[row][col] = cell.copy(isOpened = true)
            }
        }

        return newCells
    }


    private fun gameOver() {
        _gameState.update { currentState ->
            currentState.copy(
                win = false,
                gameOver = true,
            )
        }
    }

    private fun win() {
        val newCells = openedCells()

        _gameState.update { currentState ->
            currentState.copy(
                win = true,
                gameOver = false,
                cells = newCells
            )
        }
    }

    private fun openAdjacentCells(row: Int, col: Int) {
        val newCells = _gameState.value.cells.map { it.toMutableList() }.toMutableList()
        fun openCell(row: Int, col: Int) {
            for (offset in Utils.offsets) {
                val nextRow = row + offset.dy
                val nextCol = col + offset.dx

                val isValid = isValidCell(nextRow, nextCol)
                if (!isValid) continue

                val nextCell = newCells[nextRow][nextCol]
                if (!nextCell.isOpened && !nextCell.hasMine) {
                    newCells[nextRow][nextCol] = nextCell.copy(isOpened = true)

                    if (nextCell.adjacentMines ==  0) {
                        openCell(nextRow, nextCol)
                    }
                }
            }
        }

        openCell(row, col)
        _gameState.update { currentState ->
            currentState.copy(
                cells = newCells
            )
        }

        if (_gameState.value.win) return
        if (checkForWin()) {
            win()
            return
        }
    }


    fun onClickCell(cell: Cell) {
        if (_gameState.value.gameOver) return
        if (cell.isOpened || cell.isFlagged) return

        viewModelScope.launch(Dispatchers.IO) {
            val newCells = _gameState.value.cells.map { it.toMutableList() }.toMutableList()
            val newCell = cell.copy(isOpened = true)
            newCells[cell.row][cell.col] = newCell

            _gameState.update { currentState ->
                currentState.copy(
                    cells = newCells
                )
            }

            if (cell.hasMine) {
                gameOver()
                return@launch
            }

            if (checkForWin()) {
                win()
                return@launch
            }

            if (cell.adjacentMines == 0) {
                openAdjacentCells(cell.row, cell.col)
                return@launch
            }
        }
    }

    fun onLongClickCell(cell: Cell) {
        if (cell.isOpened) return
        if (availableFlagCount <= 0 && !cell.isFlagged) return

        viewModelScope.launch {
            val newCells = _gameState.value.cells.map { it.toMutableList() }.toMutableList()
            val newCell = cell.copy(isFlagged = !cell.isFlagged)
            newCells[cell.row][cell.col] = newCell

            _gameState.update { currentState ->
                var newFlagCount: Int = currentState.availableFlagCount
                if (newCell.isFlagged) newFlagCount-- else newFlagCount++
                currentState.copy(
                    availableFlagCount = newFlagCount,
                    cells = newCells,
                )
            }
        }
    }

    fun resetGame() {
        initGame()
    }
}