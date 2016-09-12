package es.flakiness.hellolatency

import java.io.File
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.CheckBox

class MainActivity : Activity() {
    val numThreads = 4
    val uiHandler = Handler()
    var senders : List<Sender> = emptyList()

    private fun stopSenders() = senders.forEach { s -> s.requestStop() }

    private fun reportResult(result: List<List<Result>>, btn: Button) {
        // TODO(morrita): Report somehow.
        btn.isEnabled = true
    }

    private fun startMeasurement(bytesToRequest: Int, btn: Button) {
        stopSenders()
        btn.isEnabled = false

        val write = (findViewById(R.id.checkbox_write) as CheckBox).isChecked
        var results : List<List<Result>> = emptyList()
        senders = (0 until numThreads).map { i ->
            val file = File(filesDir, "file-${i}.dat")
            Sender(i, bytesToRequest, 3, file, { result -> uiHandler.post {
                results = results.plusElement(result)
                if (results.size == numThreads)
                    reportResult(results, btn)
            } })
        }.map{ s -> s.start(); s }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val register = { btn: Button, bytes: Int ->
            btn.setOnClickListener { startMeasurement(bytes, btn) }
        }

        register((findViewById(R.id.start_button_10b) as Button), 10)
        register((findViewById(R.id.start_button_1k) as Button), 1024)
        register((findViewById(R.id.start_button_100k) as Button), 1024*100)
        register((findViewById(R.id.start_button_1m) as Button), 1024*1024)
        register((findViewById(R.id.start_button_4m) as Button), 1024*1024*4)
    }

    override fun onStop() {
        super.onStop()
        stopSenders()
    }
}
