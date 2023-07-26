package cab.snapp.blackBox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.d
import cab.snapp.blackBox.ui.theme.LoggerTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    companion object {
        const val LOG_LABEL = "example label"
        const val LOG_MESSAGE = "example message"
    }

    @Inject
    lateinit var logger: SnappLogger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.component.inject(this)

        setContent {
            LoggerTheme {
                Button(onClick = {
                    logger.d(LOG_LABEL, LOG_MESSAGE)
                }) {
                    Text(text = "log")
                }
            }
        }
    }
}
