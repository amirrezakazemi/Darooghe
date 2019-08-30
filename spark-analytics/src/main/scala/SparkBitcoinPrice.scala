import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.functions.{from_json, from_unixtime}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{ForeachWriter, Row}
import com.datastax.spark.connector.streaming._


object SparkBitcoinPrice extends SparkSessionBuilder {
  def main(args: Array[String]): Unit = {
    val spark = buildSparkSession
    // spark.sql.codegen.wholeStage= "false"
    val CurrentPriceSchema = new StructType(Array(
      new StructField("high", DataTypes.StringType, true),
      new StructField("last", DataTypes.StringType, true),
      new StructField("timestamp", DataTypes.StringType, true),
      new StructField("bid", DataTypes.StringType, true),
      new StructField("vwap", DataTypes.StringType, true),
      new StructField("volume", DataTypes.StringType, true),
      new StructField("low", DataTypes.StringType, true),
      new StructField("ask", DataTypes.StringType, true),
      new StructField("open", DataTypes.StringType, true)
    ))

    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")
    val inputDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("kafka.group.id", "spark-analytics")
      .option("subscribe", "coin-prices")
      .option("startingoffsets", "latest")
      .option("failOnDataLoss", "false")
      .load()

    var PriceData = inputDf.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .select($"key", from_json($"value", CurrentPriceSchema).alias("PriceData"))

    var BitcoinPriceTable = PriceData.where(PriceData.col("key").equalTo("btc-p"))
      .select($"PriceData.*").withColumn("datetime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(0, 16))
      .withColumn("secondtime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(18, 20).cast(DataTypes.IntegerType))

    val time = System.currentTimeMillis / 1000

    var BitcoinPriceLinear = PriceData.where(PriceData.col("key").equalTo("btc-p"))
      .select("PriceData.*").withColumn("time", from_unixtime($"timestamp").cast(DataTypes.TimestampType))
      .withColumn("X", $"timestamp".cast(DataTypes.LongType) - time)
      .withColumn("XY", $"X" * $"last".cast(DataTypes.DoubleType))
      .withWatermark("time", "3 seconds")
      .groupBy(window($"time", "60 seconds", "1 seconds").alias("time_interval"))
      .agg(((count("time") * sum($"XY").cast(DataTypes.LongType) -
        sum($"X").cast(DataTypes.LongType) * sum($"last").cast(DataTypes.LongType)) /
        (count("time") * sum($"X" * $"X").cast(DataTypes.LongType) - sum($"X").cast(DataTypes.LongType) * sum($"X").cast(DataTypes.LongType))).alias("slope"),

        ((count("time") * sum($"X" * $"X") - sum($"X") * sum($"XY")) /
          (count("time") * sum($"X" * $"X").cast(DataTypes.LongType) - sum($"X").cast(DataTypes.LongType) * sum($"X").cast(DataTypes.LongType))).alias("intercept")
      )
      //.withColumn("start", col("time_interval.start"))
      .withColumn("end", col("time_interval.end"))
      .withColumn("datetime", col("end").substr(0, 16))
      .withColumn("secondtime", col("end").substr(18, 20).cast(DataTypes.IntegerType))

    val BitcoinPriceSMA = PriceData.where(PriceData.col("key").equalTo("btc-p"))
      .select("PriceData.*").withColumn("time", from_unixtime($"timestamp").cast(DataTypes.TimestampType))
      .withWatermark("time", "3 seconds")
      .groupBy(window($"time", "60 seconds", "1 seconds").alias("time_interval"))
      .agg(avg($"last").alias("SMA").cast(DataTypes.DoubleType))
      //.withColumn("start", col("time_interval.start"))
      .withColumn("end", col("time_interval.end"))
      .withColumn("datetime", col("end").substr(0, 16))
      .withColumn("secondtime", col("end").substr(18, 20).cast(DataTypes.IntegerType))



    var EthereumPriceTable = PriceData.where(PriceData.col("key").equalTo("eth-p"))
      .select($"PriceData.*").withColumn("datetime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(0, 16))
      .withColumn("secondtime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(18, 20).cast(DataTypes.IntegerType))

    var FakecoinPriceTable = PriceData.where(PriceData.col("key").equalTo("fake-p"))
      .select($"PriceData.*").withColumn("datetime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(0, 16))
      .withColumn("secondtime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(18, 20).cast(DataTypes.IntegerType))

    val writeStream1 = BitcoinPriceTable.writeStream.
      foreachBatch { (batchDF, _) =>
        batchDF
          .write
          .cassandraFormat("bitcoinprice", "coinprice")
          .mode("append")
          .save
      }.start
    //    writeStream1.awaitTermination()

    val writeStream2 = EthereumPriceTable.writeStream.
      foreachBatch{ (batchDF, _) =>
        batchDF
          .write
          .cassandraFormat("ethereumprice", "coinprice")
          .mode("append")
          .save
      }.start
    //    writeStream2.awaitTermination()

    val writeStream3 = FakecoinPriceTable.writeStream.
      foreachBatch{ (batchDF, _) =>
        batchDF
          .write
          .cassandraFormat("fakecoinprice", "coinprice")
          .mode("append")
          .save
      }.start
    //    writeStream3.awaitTermination()

    var cassandraDriver:CassandraDriver = null
    val writeStream = BitcoinPriceLinear.writeStream
      .queryName("Test")
      .outputMode("update")
      .foreach(new ForeachWriter[Row] {
        override def open(partitionId: Long, epochId: Long): Boolean = {
          cassandraDriver = new CassandraDriver()
          true
        }
        override def process(value: Row): Unit = {
          cassandraDriver.connector.withSessionDo(session =>
            session.execute(s"""insert into ${cassandraDriver.namespace}.${cassandraDriver.foreachTableSink} (slope, intercept, end , datetime, secondtime) values(${value(1)}, ${value(2)},'${value(3)}', '${value(4)}', '${value(5)}')""")
          )
        }
        override def close(errorOrNull: Throwable): Unit = {
        }
      }).start
    //     writeStream.awaitTermination()


    var cassandraDriverr:CassandraDriver = null


    val writeStream4 = BitcoinPriceSMA.writeStream
      .queryName("Test2")
      .outputMode("update")
      .foreach(new ForeachWriter[Row] {
        override def open(partitionId: Long, epochId: Long): Boolean = {
          cassandraDriverr = new CassandraDriver()
          true
        }
        override def process(value: Row): Unit = {
          cassandraDriverr.connector.withSessionDo(session =>
            session.execute(s"""insert into ${cassandraDriverr.namespace}.${cassandraDriverr.smaTable} (sma, end, datetime, secondtime) values(${value(1)}, '${value(2)}', '${value(3)}', ${value(4)})""")
          )
        }
        override def close(errorOrNull: Throwable): Unit = {
        }
      }).start
    writeStream4.awaitTermination()
  }
}
