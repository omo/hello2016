import java.io.RandomAccessFile
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Okio

object Hi {
  def main(args: Array[String]) = {
    val file = new File("./data.out")
    val sink = Okio.sink(file)

    val cli = new OkHttpClient()
    val req = new Request.Builder().url("http://example.com/").build()
    val res = cli.newCall(req).execute()
    val src = res.body.source.readAll(sink)

    sink.flush()

    val raf = new RandomAccessFile(file, "r")
    println("Body Length:" + raf.length)
  }
}
