import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.functions.{from_json, from_unixtime}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}


object SparkBitcoinPrice extends SparkSessionBuilder {
  def main(args: Array[String]): Unit = {
    val spark = buildSparkSession
    //spark.sql.codegen.wholeStage= "false"
    val time = System.currentTimeMillis();
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
      .option("subscribe", "coin-prices")
      .option("startingoffsets", "latest")
      //.option("failOnDataLoss", "false")
      .load()
    //var BitcoinPriceKey = inputDf.selectExpr("CAST(key AS STRING)")

    var PriceData = inputDf.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .select($"key", from_json($"value", CurrentPriceSchema).alias("PriceData"))

    var BitcoinPriceTable = PriceData.where(PriceData.col("key").equalTo("btc-p"))
      .select($"PriceData.*").withColumn("datetime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(0, 16))
      .withColumn("secondtime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(18, 20).cast(DataTypes.IntegerType))

    var EthereumPriceTable = PriceData.where(PriceData.col("key").equalTo("eth-p"))
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

    val writeStream2 = EthereumPriceTable.writeStream.
      foreachBatch{ (batchDF, _) =>
        batchDF
          .write
          .cassandraFormat("ethereumprice", "coinprice")
          .mode("append")
          .save
      }.start

    writeStream2.awaitTermination()
  }
}
