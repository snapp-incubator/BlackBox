package cab.snapp.blackBox.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import cab.snapp.blackBox.R
import java.io.File

class FileShareUtils(private val context: Context) {

    companion object {
        const val PROVIDER_SUFFIX = ".provider"
        const val INTENT_TYPE = "text/plain"
        const val MailTo = "mailto:"
    }

    fun shareFile(file: File, subject: String = "", message: String = "") {
        val fileUri =
            FileProvider.getUriForFile(context, context.packageName + PROVIDER_SUFFIX, file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = INTENT_TYPE
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)

        val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share_file))
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    fun sendEmail(emails: Array<String>, subject: String = "", message: String = "", file: File) {
        val fileUri =
            FileProvider.getUriForFile(context, context.packageName + PROVIDER_SUFFIX, file)

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse(MailTo)
        intent.putExtra(Intent.EXTRA_EMAIL, emails)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)

        val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share_email))
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}
