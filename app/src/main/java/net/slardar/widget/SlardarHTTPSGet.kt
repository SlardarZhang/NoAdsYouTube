package net.slardar.widget

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
        const val TIMEOUT: Int = 10 * 1000
        const val BUFFER_SIZE: Int = 4096
        fun getHTML(url: String?, handler: Handler?) {
            object : Thread() {
                override fun run() {
                    val msg = Message()
                    try {
                        val connect: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
                        connect.requestMethod = "GET"
                        connect.readTimeout = TIMEOUT
                        connect.connectTimeout = TIMEOUT
                        connect.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                        connect.connect()

                        if (connect.responseCode == HttpURLConnection.HTTP_OK) {
                            var charset: String? = if (connect.getHeaderField("content-type").equals("")) {
                                ""
                            } else {
                                if (connect.getHeaderField("content-type").indexOf("charset=") != -1) {
                                    connect.getHeaderField("content-type")
                                        .substring(connect.getHeaderField("content-type").indexOf("charset=") + 8)
                                } else {
                                    ""
                                }
                            }
                            charset = if (!charset.equals("")) {
                                ""
                            } else {
                                if (charset!!.indexOf(";") != -1) {
                                    charset.substring(charset.indexOf(";"))
                                } else {
                                    charset
                                }
                            }
                            msg.arg1 = 0
                            msg.obj = readStream(connect.inputStream, charset)
                        } else {
                            msg.arg1 = -1
                            msg.obj = url
                            msg.obj = readStream(connect.errorStream, "")
                        }
                    } catch (ex: Exception) {
                        msg.arg1 = -1
                        msg.obj = ex.message + Log.getStackTraceString(ex)
                    }
                    handler!!.sendMessage(msg)
                }
            }.start()
        }

        fun readStream(iss: InputStream, htmlCharset: String?): String? {
            val sb = StringBuilder()
            var line: String?
            val buffer = ByteArray(BUFFER_SIZE)
            var read: Int? = 1
            while (read!! > 0) {
                buffer.fill(0, 0, BUFFER_SIZE)
                read = iss.read(buffer, 0, BUFFER_SIZE)
                if (read > 0) {
                    line = if (htmlCharset.equals("")) {
                        String(buffer, 0, read)
                    } else {
                        String(buffer, 0, read, charset(htmlCharset!!))
                    }
                    sb.append(line)
                }
            }
            return sb.toString()
        }
    }
}