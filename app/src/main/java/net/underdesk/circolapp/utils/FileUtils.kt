/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.underdesk.circolapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent.*
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.adapters.CircularLetterAdapter

object FileUtils {
    fun viewFile(url: String, context: Context, session: CustomTabsSession?) {
        if (url.endsWith(".pdf")) {
            val viewIntent = Intent(Intent.ACTION_VIEW)
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

            context.startActivity(viewIntent)
        } else {
            val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)

            val otherParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(primaryColor)
                .build()

            val customTabsIntent = Builder(session)
                .setShowTitle(true)
                .setColorScheme(COLOR_SCHEME_SYSTEM)
                .setDefaultColorSchemeParams(otherParams)
                .setShareState(SHARE_STATE_ON)
                .build()

            customTabsIntent.launchUrl(context, Uri.parse(url))
        }
    }

    fun preloadFiles(url: String?, otherUrls: List<String>, session: CustomTabsSession?) {
        if (url == null)
            return

        if (!url.endsWith(".pdf")) {
            val bundles = arrayListOf<Bundle>()

            for (otherUrl in otherUrls) {
                if (!url.endsWith(".pdf")) {
                    val bundle = Bundle().apply {
                        putParcelable(CustomTabsService.KEY_URL, Uri.parse(otherUrl))
                    }

                    bundles.add(bundle)
                }
            }

            session?.mayLaunchUrl(Uri.parse(url), null, bundles)
        }
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
