package net.underdesk.circolapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.adapters.CircularLetterAdapter

object FileUtils {
    fun viewFile(url: String, context: Context) {
        val viewIntent = Intent(Intent.ACTION_VIEW)

        if (url.endsWith(".pdf")) {
            viewIntent.setDataAndType(Uri.parse(url), "application/pdf")

            if (viewIntent.resolveActivity(context.packageManager) == null) {
                val builder = MaterialAlertDialogBuilder(context)
                builder.apply {
                    setTitle(R.string.dialog_install_pdf_reader_title)
                    setMessage(R.string.dialog_install_pdf_reader_content)
                    setPositiveButton(
                        R.string.dialog_ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                }

                builder.create().show()

                return
            }
        } else {
            viewIntent.data = Uri.parse(url)
        }

        context.startActivity(viewIntent)
    }

    fun shareFile(url: String, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun downloadFile(
        file: DownloadableFile,
        adapterCallback: CircularLetterAdapter.AdapterCallback,
        context: Context
    ) {
        adapterCallback.fileToDownload = file

        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            val builder = MaterialAlertDialogBuilder(context)

            builder.apply {
                setMessage(context.getString(R.string.dialog_message_permission_write))
                setTitle(context.getString(R.string.dialog_title_permission_required))
                setPositiveButton(
                    context.getString(R.string.dialog_next)
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        adapterCallback as AppCompatActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                    )
                }
            }

            builder.create().show()
        } else {
            adapterCallback.downloadFile()
        }
    }
}

data class DownloadableFile(
    val name: String,
    val url: String
)
