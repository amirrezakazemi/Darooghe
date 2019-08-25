import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class CassandraDriver extends SparkSessionBuilder {

  val spark = buildSparkSession

  import spark.implicits._

  val connector = CassandraConnector(spark.sparkContext.getConf)

  val namespace = "coinprice"

  val foreachTableSink = "bitcoinpricelinear"

  val smaTable = "bitcoinpricesma"

}
