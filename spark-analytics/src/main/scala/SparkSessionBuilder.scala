import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkSessionBuilder extends Serializable {
  def buildSparkSession: SparkSession = {

    @transient lazy val conf: SparkConf = new SparkConf(true)

      .setAppName("Structured Streaming from Kafka to Cassandra")

      .set("spark.cassandra.connection.host", "localhost")

      .set("spark.sql.streaming.checkpointLocation", "checkpoint")

      .set("spark.sql.codegen.wholeStage","false")

    @transient lazy val spark = SparkSession

      .builder()
      .master("local[*]")
      .config(conf)
      .getOrCreate()


    spark

  }

}