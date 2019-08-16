import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import com.datastax.spark.connector.streaming._
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.joda.time.{DateTimeZone}
import org.joda.time.format.DateTimeFormat

object SparkHistBitcoinPrice extends SparkSessionBuilder {
  def main(args: Array[String]): Unit = {
    val spark = buildSparkSession
    val BitcoinHourPriceSchema = new StructType(Array(
      new StructField("time", DataTypes.LongType, true),
      new StructField("close", DataTypes.DoubleType, true),
      new StructField("high", DataTypes.DoubleType, true),
      new StructField("low", DataTypes.DoubleType, true),
      new StructField("open", DataTypes.DoubleType, true),
      new StructField("volumefrom", DataTypes.DoubleType, true),
      new StructField("volumeto", DataTypes.DoubleType, true)))


    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")
    val inputDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "bitcoin-price")
      .option("startingoffsets" , "earliest")
      //.option("failOnDataLoss", "false")
      .load()
    var BitcoinPriceKey = inputDf.selectExpr("CAST(key AS STRING)")

    var BitcoinPriceValue = inputDf.selectExpr("CAST(value AS STRING)")

    var BitcoinPriceData = BitcoinPriceValue.select(from_json($"value", BitcoinHourPriceSchema).alias("BitcoinPriceData"))

    var BitcoinPriceDefinedTable = BitcoinPriceData.select("BitcoinPriceData.*")

    var BitcoinCloseAvg = BitcoinPriceDefinedTable
        .withColumn("time", from_unixtime($"time").cast(DataTypes.TimestampType))
      .withWatermark("time", "10 seconds")
      .groupBy(window($"time", "36000 seconds", "3600 seconds"))
      .agg(avg($"close"))



    val writeStream = BitcoinCloseAvg.writeStream
      .format("console")
      .outputMode("update")
      .option("truncate", "false")
      .start()
    writeStream.awaitTermination()
      // write data to cassandra with out aggregation
    /*
val writeStream = BitcoinPriceDefinedTable.writeStream.
  foreachBatch{ (batchDF, _) =>
    batchDF
      .write
      .cassandraFormat("histprice", "bitcoin")
      .mode("append")
      .save
  }.start
    writeStream.awaitTermination()
*/
    ///// wite data to cassandra (aggreagation)
    /*
      var cassandraDriver:CassandraDriver = null
      val writeStream = transactionInputValues.writeStream
        .queryName("Test")
        .outputMode("update")
        .foreach(new ForeachWriter[Row] {
          override def open(partitionId: Long, epochId: Long): Boolean = {
            cassandraDriver = new CassandraDriver()
            true
          }

          override def process(value: Row): Unit = {
            cassandraDriver.connector.withSessionDo(session =>
              session.execute(s"""insert into ${cassandraDriver.namespace}.${cassandraDriver.foreachTableSink} (tx_index, suminputvalue) values('${value(0)}', '${value(1)}')""")
            )
          }

          override def close(errorOrNull: Throwable): Unit = {

          }
        }).start
      writeStream.awaitTermination()
  */










  }

}
