package com.dannndi.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dannndi.playground.ui.pages.minesweeper.MinesweeperScreen
import com.dannndi.playground.ui.pages.minesweeper.MinesweeperViewModel
import com.dannndi.playground.ui.theme.ComposePlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                val gameViewModel: MinesweeperViewModel = viewModel()
                val gameState by gameViewModel.gameState.collectAsState()

                MinesweeperScreen(
                    gameState = gameState,
                    onClick = { cell ->
                        gameViewModel.onClickCell(cell)
                    },
                    onLongClick = { cell ->
                        gameViewModel.onLongClickCell(cell)
                    },
                    onResetClicked = {
                        gameViewModel.resetGame()
                    }
                )
            }
        }
    }
}

