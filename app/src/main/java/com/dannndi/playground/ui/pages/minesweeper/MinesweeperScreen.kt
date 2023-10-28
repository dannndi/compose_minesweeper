package com.dannndi.playground.ui.pages.minesweeper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dannndi.playground.data.Cell
import com.dannndi.playground.ui.theme.Green
import com.dannndi.playground.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinesweeperScreen(
    gameState: MinesweeperState,
    onClick: (Cell) -> Unit,
    onLongClick: (Cell) -> Unit,
    onResetClicked: () -> Unit,
) {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = gameState.win) {
        if (gameState.win) {
            hostState.showSnackbar("Congratulation, you win :D")
        }
    }

    LaunchedEffect(key1 = gameState.gameOver) {
        if (gameState.gameOver) {
            hostState.showSnackbar("Game Over xD")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = "Minesweeper",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 24.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Flag",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )

                    Text(
                        text = "${gameState.availableFlagCount}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }

                Button(
                    onClick = onResetClicked,
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset")
                }
            }
            Game(
                gameState = gameState,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Game(
    gameState: MinesweeperState,
    onClick: (Cell) -> Unit,
    onLongClick: (Cell) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(gameState.col),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            count = gameState.col * gameState.row,
            key = { index ->
                val row = index.div(gameState.col)
                val col = index.mod(gameState.col)
                val cell = gameState.cells[row][col]
                "${cell.row}${cell.col}"
            }
        ) { index ->
            val row = index.div(gameState.col)
            val col = index.mod(gameState.col)
            val cell = gameState.cells[row][col]

            CellComp(
                cell = cell,
                win = gameState.win,
                gameOver = gameState.gameOver,
                modifier = Modifier
                    .combinedClickable(
                        onClick = { onClick(cell) },
                        onLongClick = { onLongClick(cell) })
            )
        }
    }
}


@Composable
fun CellComp(
    cell: Cell,
    win: Boolean,
    gameOver: Boolean,
    modifier: Modifier = Modifier
) {
    val color = when {
        cell.isOpened && cell.hasMine && win -> Green
        cell.isOpened && cell.hasMine && gameOver -> Red
        cell.isOpened -> Color.LightGray
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier.aspectRatio(1f),
        color = color,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val text: String = when {
                cell.isFlagged -> "🚩"
                cell.isOpened && cell.hasMine -> "💣"
                cell.isOpened && !cell.hasMine -> "${cell.adjacentMines}"
                else -> ""
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

//@Preview
//@Composable
//fun MinesweeperScreenPreview() {
//    val state = MinesweeperState()
//    // create cells
//    val cells = mutableListOf<MutableList<Cell>>()
//    for (row in 0 until state.row) {
//        val rows = mutableListOf<Cell>()
//        for (col in 0 until state.col) {
//            rows.add(Cell(row = row, col = col, isOpened = false))
//        }
//        cells.add(rows)
//    }
//
//
//    MinesweeperScreen(
//        gameState = state.copy(cells = cells),
//        onClick = {},
//        onLongClick = {},
//        onResetClicked = {}
//    )
//}