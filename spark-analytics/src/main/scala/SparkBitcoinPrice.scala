import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

object SparkBitcoinPrice extends SparkSessionBuilder {
  def main(args: Array[String]): Unit = {
    val spark = buildSparkSession

    val BitcoinCurrentPriceSchema = new StructType(Array(
      StructField("time", StructType(Array(
        StructField("updated", DataTypes.StringType, true),
        StructField("updatedISO", DataTypes.StringType, true),
        StructField("updateduk", DataTypes.StringType, true)
      )), true),
      StructField("disclaimer", DataTypes.StringType, true),
      StructField("chartName", DataTypes.StringType, true),
      StructField("bpi", StructType(Array(
        StructField("USD", StructType(Array(
          StructField("code", DataTypes.StringType, true),
          StructField("symbol", DataTypes.StringType, true),
          StructField("rate", DataTypes.StringType, true),
          StructField("description", DataTypes.StringType, true),
          StructField("rate_float", DataTypes.StringType, true)
        )), true),
        StructField("GBP", StructType(Array(
          StructField("code", DataTypes.StringType, true),
          StructField("symbol", DataTypes.StringType, true),
          StructField("rate", DataTypes.StringType, true),
          StructField("description", DataTypes.StringType, true),
          StructField("rate_float", DataTypes.StringType, true)
        )), true),
        StructField("EUR", StructType(Array(
          StructField("code", DataTypes.StringType, true),
          StructField("symbol", DataTypes.StringType, true),
          StructField("rate", DataTypes.StringType, true),
          StructField("description", DataTypes.StringType, true),
          StructField("rate_float", DataTypes.StringType, true)
        )), true)
      )), true)
    ))

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

    var BitcoinPriceValue = inputDf.selectExpr("CAST(value AS STRING)", "CAST(timestamp AS TIMESTAMP)")

    var BitcoinPriceData = BitcoinPriceValue.select(from_json($"value", BitcoinCurrentPriceSchema).alias("BitcoinPriceData"), $"timestamp")

    var BitcoinPriceDefinedTable = BitcoinPriceData.select($"BitcoinPriceData.bpi.USD.rate_float", $"timestamp")

    val writeStream = BitcoinPriceDefinedTable.writeStream
      .format("console")
      .outputMode("append")
      .start()
    writeStream.awaitTermination()













  }

}
