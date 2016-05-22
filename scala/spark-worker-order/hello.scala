
import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}

object Hello extends App {
  val master = if (args.isDefinedAt(0)) args(0) else null
  val conf = new SparkConf().setAppName("Hello")
  if (master != null)
    conf.setMaster(master)
  val sc = new SparkContext(conf)
  val list = sc.parallelize(List(0,1,2,3,4,5,6,7,8,9))
  list.repartition(list.count().asInstanceOf[Int]).foreach { (i) =>
    println(s"Started index; ${i}")
    Thread.sleep(if (i % 4 == 0) (10 - i) * 500 else 0)
    println(s"Finished index; ${i}")
  }
}