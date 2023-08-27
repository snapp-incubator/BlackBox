package cab.snapp.blackBox

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cab.snapp.blackBox.components.LogReportPeriodDialog
import cab.snapp.blackBox.poaro.LogReportGenerator
import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.d
import cab.snapp.blackBox.ui.theme.LoggerTheme
import cab.snapp.blackBox.utils.FileShareUtils
import java.io.File
import javax.inject.Inject


class MainActivity : ComponentActivity() {

    companion object {
        const val REPORT_FILE_NAME = "report.log"
        const val LOG_LABEL = "something"
        const val LOG_MESSAGE = "something happened"
        const val SAMPLE_EMAIL = "sample@gmail.com"
    }

    @Inject
    lateinit var logger: SnappLogger

    @Inject
    lateinit var logReportGenerator: LogReportGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.component.inject(this)

        setContent {
            var showReportDialog by remember { mutableStateOf(false) }
            val onPeriodSelected = remember {
                { selectedItem: Int ->
                    createAndShareReport(selectedItem, REPORT_FILE_NAME)
                }
            }
            val dismissDialog = remember {
                {
                    showReportDialog = false
                }
            }
            val onGetReportClicked = remember {
                {
                    showReportDialog = true
                }
            }
            LoggerTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.margin_standard)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            logger.d(LOG_LABEL, LOG_MESSAGE)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.add_log_button))
                    }

                    Button(
                        onClick = {
                            onGetReportClicked.invoke()
                        },
                        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.margin_small))
                    ) {
                        Text(text = stringResource(id = R.string.generate_report_button))
                    }
                }
                if (showReportDialog) {
                    LogReportPeriodDialog(onPeriodSelected, dismissDialog)
                }
            }
        }
    }

    private fun createAndShareReport(timeRange: Int, outputFileName: String) {
        val file = logReportGenerator.createAndGetReport(timeRange, outputFileName)
        shareReport(file)
    }

    private fun shareReport(reportFile : File) {
        if (reportFile.exists()) {
            val fileShareUtils = FileShareUtils(this@MainActivity)

            fileShareUtils.shareFile(reportFile, resources.getString(R.string.share_report))
            fileShareUtils.sendEmail(
                arrayOf(SAMPLE_EMAIL),
                resources.getString(R.string.report_email_subject),
                resources.getString(R.string.report_email_message),
                reportFile
            )
        } else {
            Toast.makeText(
                this@MainActivity,
                resources.getString(R.string.report_file_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoggerTheme {
        Greeting("Android")
    }
}