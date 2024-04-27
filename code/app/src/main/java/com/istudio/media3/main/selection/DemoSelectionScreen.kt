package com.istudio.media3.main.selection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DemoSelectionScreen() {

    // Retrieve the CounterViewModel instance using viewModel()
    val counterViewModel: SelectionScreenViewModel = viewModel()


    // Handle back button presses
    BackHandler {
        // Handle back button presses here if needed
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select your Demo")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {  }) {
            Text(text = "Demo-1")
        }

    }

}