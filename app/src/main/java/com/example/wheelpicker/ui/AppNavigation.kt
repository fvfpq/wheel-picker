package com.example.wheelpicker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheelpicker.data.OptionRepository
import com.example.wheelpicker.ui.backdoor.BackdoorPasswordScreen
import com.example.wheelpicker.ui.backdoor.BackdoorScreen
import com.example.wheelpicker.ui.backdoor.BackdoorViewModel
import com.example.wheelpicker.ui.edit.EditScreen
import com.example.wheelpicker.ui.edit.EditViewModel
import com.example.wheelpicker.ui.history.HistoryScreen
import com.example.wheelpicker.ui.history.HistoryViewModel
import com.example.wheelpicker.ui.wheel.WheelScreen
import com.example.wheelpicker.ui.wheel.WheelViewModel

@Composable
fun AppNavigation(repository: OptionRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "wheel") {

        composable("wheel") {
            val viewModel: WheelViewModel = viewModel(factory = wheelFactory(repository))
            WheelScreen(
                viewModel = viewModel,
                onOpenEdit = { navController.navigate("edit") },
                onOpenHistory = { navController.navigate("history") },
                onOpenBackdoor = { navController.navigate("backdoor") },
            )
        }

        composable("edit") {
            val viewModel: EditViewModel = viewModel(factory = editFactory(repository))
            EditScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable("history") {
            val viewModel: HistoryViewModel = viewModel(factory = historyFactory(repository))
            HistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable("backdoor") {
            val viewModel: BackdoorViewModel = viewModel(factory = backdoorFactory(repository))
            BackdoorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenPassword = { navController.navigate("backdoor_password") },
            )
        }

        composable("backdoor_password") {
            val viewModel: BackdoorViewModel = viewModel(factory = backdoorFactory(repository))
            BackdoorPasswordScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private fun wheelFactory(repository: OptionRepository) = viewModelFactory {
    initializer { WheelViewModel(repository) }
}

private fun editFactory(repository: OptionRepository) = viewModelFactory {
    initializer { EditViewModel(repository) }
}

private fun historyFactory(repository: OptionRepository) = viewModelFactory {
    initializer { HistoryViewModel(repository) }
}

private fun backdoorFactory(repository: OptionRepository) = viewModelFactory {
    initializer { BackdoorViewModel(repository) }
}
