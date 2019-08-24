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
      .option("subscribe", "bitcoin-transactions")
      .option("startingoffsets", "latest")
      //.option("failOnDataLoss", "false")
      .load()
    //var BitcoinPriceKey = inputDf.selectExpr("CAST(key AS STRING)")

    var PriceData = inputDf.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .select($"key", from_json($"value", CurrentPriceSchema).alias("PriceData"))

    var BitcoinPriceTable = PriceData.where(PriceData.col("key").equalTo("bitcoin-price"))
      .select($"PriceData.*").withColumn("datetime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(0, 16))
      .withColumn("secondtime", from_unixtime($"timestamp").cast(DataTypes.TimestampType).substr(18, 20).cast(DataTypes.IntegerType))



/*

    var bitcoinPriceSlope = PriceData.where(PriceData.col("key").equalTo("bitcoin-price"))
      .select("PriceData.*").withColumn("time", from_unixtime($"timestamp").cast(DataTypes.TimestampType))
      .withWatermark("time", "1 seconds")
      .groupBy(window($"time", "30 seconds", "5 seconds"))
      .agg(sum($"timestamp".cast(DataTypes.TimestampType)))
        //.cast(DataTypes.TimestampType).cast(DataTypes.LongType)- time))
*/




    var EthereumPriceTable = PriceData.where(PriceData.col("key").equalTo("ethereum-price"))
      .select($"PriceData.*").withColumn("time", from_unixtime($"timestamp").cast(DataTypes.TimestampType))


    val writeStream1 = BitcoinPriceTable.writeStream.
      foreachBatch { (batchDF, _) =>
        batchDF
          .write
          .cassandraFormat("bitcoinprice", "coinprice")
          .mode("append")
          .save
      }.start
    writeStream1.awaitTermination()




      val writeStream2 = EthereumPriceTable.writeStream.
        foreachBatch{ (batchDF, _) =>
          batchDF
            .write
            .cassandraFormat("ethereumprice", "coinprice")
            .mode("append")
            .save
        }.start
      writeStream2.awaitTermination()




/*

      val writeStream = bitcoinPriceSlope.writeStream
        .format("console")
        .outputMode("update")
        .option("truncate", "false")
        .start()
      writeStream.awaitTermination()
*/

    }



}
