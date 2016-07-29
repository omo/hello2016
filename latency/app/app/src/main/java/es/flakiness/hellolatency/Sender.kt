package es.flakiness.hellolatency

import android.os.SystemClock
import android.os.Trace
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HttpsURLConnection

class Result(val connMillis: Long, val downloadMillis: Long)

class Sender(
        val index: Int,
        val requestBytes: Int,
        val requestCount: Int,
        val toWrite: File,
        val done: (List<Result>) -> Unit) : Thread() {
    private val TAG = "LatencySender"
    private val url = URL("https://hello-latency.appspot.com/?len=" + requestBytes)
    private val running = AtomicBoolean()

    fun requestStop() = running.set(false)

    private inline fun <T> withTrace(section: String, op : () -> T) : Pair<T, Long> {
        Trace.beginSection(section);
        try {
            val start = SystemClock.uptimeMillis()
            return Pair(op(), SystemClock.uptimeMillis() - start)
        } finally {
            Trace.endSection()
        }
    }

    override fun run() {
        running.set(true)
        var results = emptyList<Result>()
        val buffer = ByteArray(1024)
        for (i in (0 until requestCount)) {
            if (!running.get())
                return;

            val resp = withTrace<InputStream>("open:" + i, {
                val conn = url.openConnection() as HttpsURLConnection
                conn.inputStream
            })

            val reads = withTrace<Unit>("read:" + i, {
                val out = if (toWrite != null) BufferedOutputStream(FileOutputStream(toWrite)) else null
                var total = 0
                while (total < requestBytes) {
                    val read = resp.first.read(buffer)
                    if (read == -1)
                        break;
                    total += read
                    out?.write(buffer, 0, read)
                }

                out?.close()
                toWrite?.delete()
            })

            val r = Result(resp.second, reads.second)
            if (Log.isLoggable(TAG, Log.DEBUG))
                Log.d(TAG, "i=${i} conn=${r.connMillis}ms, dl=${r.downloadMillis}ms")
            results = results.plus(r)
        }

        done(results)
    }
}