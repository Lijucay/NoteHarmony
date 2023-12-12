package com.lijukay.noteharmony.utils.handler

import android.content.Context
import android.os.Build
import com.lijukay.noteharmony.BuildConfig
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

class UncaughtExceptionHandler(
    private val context: Context
): Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        val logs = e.message
        val logsWriter = StringWriter()
        e.printStackTrace(PrintWriter(logsWriter))

        if (logs != null) {
            try {
                val fos = context.openFileOutput("logs.txt", Context.MODE_PRIVATE)
                fos.write(
                    ("------Message------" +
                        "\n\n$e" +
                        "\n\n------Logs------" +
                        "\n\n${logsWriter.toString().replace(",", ",\n")}" +
                        "\n\n------App information------" +
                        "\n\nVersion code: ${BuildConfig.VERSION_CODE}" +
                        "\nVersion name: ${BuildConfig.VERSION_NAME}" +
                        "\n\n------Device information------" +
                        "\nAndroid version: ${Build.VERSION.RELEASE}" +
                        "\nAndroid SDK Integer: ${Build.VERSION.SDK_INT}" +
                        "\nDevice: ${Build.DEVICE}" +
                        "\nDevice brand: ${Build.BRAND}" +
                        "\nDevice model: ${Build.MODEL}"
                    ).toByteArray()
                )
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // TODO: Add LogActivity to show logs
        /*
        * val intent = Intent(context, LogActivity::class.java)
        *
        * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK)
        * intent.putExtra("filePath", "${context.filesDir}${File.separator}logs.txt")
        * intent.putExtra("logs", logs)
        * intent.putExtra("detailedLogs", logWriter.toString())
        * context.startActivity(intent)
        *
        * System.exit(1)
        * */
    }
}