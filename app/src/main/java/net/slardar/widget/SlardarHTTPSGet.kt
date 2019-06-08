package net.slardar.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class SlardarHTTPSGet {
    companion object {
        private const val TIMEOUT: Int = 10 * 1000
        private const val BUFFER_SIZE: Int = 4096
        fun getStringThread(url: String, handler: Handler, arg1: Int) {
            getStringThread(url, HashMap<String, String>(), handler, arg1)
        }

        fun getStringThread(url: String, header: Map<String, String>, handler: Handler, arg1: Int) {
            Log.wtf("URL", url)
            Thread(Runnable {

                val msg = Message()
                try {
                    val connect: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
                    connect.requestMethod = "GET"
                    connect.readTimeout = TIMEOUT
                    connect.connectTimeout = TIMEOUT
                    connect.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                    connect.setRequestProperty("Cache-Control", "no-cache")
                    connect.defaultUseCaches = false
                    connect.useCaches = false
                    header.forEach {
                        connect.setRequestProperty(it.key, it.value)
                    }
                    connect.connect()

                    if (connect.responseCode == HttpURLConnection.HTTP_OK) {
                        var charset: String = if (connect.getHeaderField("content-type").isEmpty()) {
                            ""
                        } else {
                            if (connect.getHeaderField("content-type").indexOf("charset=") != -1) {
                                connect.getHeaderField("content-type")
                                    .substring(connect.getHeaderField("content-type").indexOf("charset=") + 8)
                            } else {
                                ""
                            }
                        }
                        charset = if (charset.isEmpty()) {
                            ""
                        } else {
                            if (charset.indexOf(";") != -1) {
                                charset.substring(charset.indexOf(";"))
                            } else {
                                charset
                            }
                        }
                        msg.arg1 = arg1
                        msg.obj = readStream(connect.inputStream, charset)
                    } else {
                        msg.arg1 = -1
                        msg.obj = url
                        msg.obj = readStream(connect.errorStream, "")
                    }
                } catch (ex: Exception) {
                    Log.wtf("Get HTML", ex)
                    msg.arg1 = -1
                    msg.obj = ex.message + Log.getStackTraceString(ex)
                }
                handler.sendMessage(msg)
            }).start()
        }

        fun getBitmap(url: String): Bitmap? {
            return getBitmap(url, HashMap())
        }

        private fun getBitmap(url: String, header: Map<String, String>): Bitmap? {
            try {
                val connect: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
                connect.requestMethod = "GET"
                connect.readTimeout = TIMEOUT
                connect.connectTimeout = TIMEOUT
                connect.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                connect.setRequestProperty("Cache-Control", "no-cache")
                connect.defaultUseCaches = false
                connect.useCaches = false
                header.forEach {
                    connect.setRequestProperty(it.key, it.value)
                }
                connect.connect()
                if (connect.responseCode == HttpURLConnection.HTTP_OK) {
                    return BitmapFactory.decodeStream(connect.inputStream)
                } else {
                    throw Exception("responseCode:" + connect.responseCode.toString())
                }
            } catch (ex: Exception) {
                Log.wtf("Get ByteArray Error", ex)
                return null
            }
        }

        private fun readStream(iss: InputStream, htmlCharset: String): String {
            val sb = StringBuilder()
            var line: String
            val buffer = ByteArray(BUFFER_SIZE)
            var read = 1
            while (read > 0) {
                buffer.fill(0, 0, BUFFER_SIZE)
                read = iss.read(buffer, 0, BUFFER_SIZE)
                if (read > 0) {
                    line = if (htmlCharset.isEmpty()) {
                        String(buffer, 0, read)
                    } else {
                        String(buffer, 0, read, charset(htmlCharset))
                    }
                    sb.append(line)
                }
            }
            return sb.toString()
        }
    }
}