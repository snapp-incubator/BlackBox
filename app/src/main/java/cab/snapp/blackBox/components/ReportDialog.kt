package cab.snapp.blackBox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import cab.snapp.blackBox.R
import kotlinx.coroutines.delay

@Composable
internal fun LogReportPeriodDialog(onPeriodSelected: (Int) -> Unit, dismissDialog: () -> Unit) {
    var selectedTimePeriod by remember { mutableStateOf(0) }
    var showErrorToast by remember { mutableStateOf(false) }

    val onSelectedTimePeriodChanged = remember {
        { selectedItem: Int ->
            selectedTimePeriod = selectedItem
        }
    }
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = {
                if (selectedTimePeriod != 0) {
                    onPeriodSelected.invoke(selectedTimePeriod)
                    dismissDialog.invoke()
                } else {
                    showErrorToast = true
                }
            }) {
                Text(stringResource(id = R.string.time_range_title))
            }
        },
        dismissButton = {
            Button(onClick = { dismissDialog.invoke() }) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = {
            Text(stringResource(id = R.string.description))
        },
        text = {
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.margin_small)),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_standard)))
                RadioGroup(selectedTimePeriod, onSelectedTimePeriodChanged)
            }
        }
    )

    if (showErrorToast) {
        ShowErrorToast(
            message = stringResource(id = R.string.select_time_error),
            onDismiss = { showErrorToast = false }
        )
    }
}

@Composable
private fun RadioGroup(selectedPeriod: Int, onSelected: (Int) -> Unit) {
    val timeRanges = listOf(30, 60, 120)

    timeRanges.forEach { timeRange ->
        Row(verticalAlignment = Alignment.CenterVertically) { // Align items vertically in the row
            RadioButton(
                selected = selectedPeriod == timeRange,
                onClick = { onSelected(timeRange) }
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_small)))
            Text("$timeRange ".plus(stringResource(id = R.string.minutes)))
        }
    }
}

@Composable
fun ShowErrorToast(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(true) {
        delay(2000) // Delay for 2 seconds
        onDismiss() // Dismiss the toast
    }

    Snackbar(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.margin_standard)),
        content = { Text(message) },
    )
}