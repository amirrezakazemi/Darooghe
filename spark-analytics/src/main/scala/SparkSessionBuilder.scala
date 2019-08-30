import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkSessionBuilder extends Serializable {
  def buildSparkSession: SparkSession = {

    @transient lazy val conf: SparkConf = new SparkConf(true)

      .setAppName("Structured Streaming from Kafka to Cassandra")

      .set("spark.cassandra.connection.host", "172.18.0.3, 127.18.0.4")

      .set("spark.sql.streaming.checkpointLocation", "checkpoint")

      .set("spark.sql.codegen.wholeStage","false")

    @transient lazy val spark = SparkSession

      .builder()
//      .master("local[*]")
//      .master("spark://localhost:7077")
      .config(conf)
      .getOrCreate()


    spark

  }

}