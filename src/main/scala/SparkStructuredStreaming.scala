
import org.apache.spark.sql.SparkSession
import org.apache.log4j
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{ArrayType, DataTypes, StringType, StructField, StructType}


object SparkStructuredStreaming {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("kafka-tutorials")
      .master("local[*]")
      .getOrCreate()

    val schema = StructType(Array(StructField("payload",
      StructType(Array(
        StructField("txid", DataTypes.StringType, true),
        StructField("hash", DataTypes.StringType, true),
        StructField("index", DataTypes.StringType, true),
        StructField("version", DataTypes.StringType, true),
        StructField("size", DataTypes.StringType, true),
        StructField("vsize", DataTypes.StringType, true),
        StructField("locktime", DataTypes.StringType, true),
        StructField("time", DataTypes.StringType, true),
        StructField("blockhash", DataTypes.StringType, true),
        StructField("blockheight", DataTypes.StringType, true),
        StructField("blocktime", DataTypes.StringType, true),
        StructField("timestamp", DataTypes.StringType, true),
        StructField("confirmations", DataTypes.StringType, true),



        StructField("txins", ArrayType(StructType(Array(
          StructField("txout", DataTypes.StringType, true),
          StructField("vout", DataTypes.StringType, true),
          StructField("amount", DataTypes.StringType, true),
          StructField("addresses", ArrayType(DataTypes.StringType, true), true),
          StructField("script", StructType(Array(
            StructField("asm", DataTypes.StringType, true),
            StructField("hex", DataTypes.StringType, true),

          )), true),
          StructField("votype", DataTypes.StringType, true))), true)),

        StructField("txouts", ArrayType(StructType(Array(
          StructField("amount", DataTypes.StringType, true),
          StructField("type", DataTypes.StringType, true),
          StructField("spent", DataTypes.StringType, true),
          StructField("addresses", ArrayType(DataTypes.StringType, true), true),
          StructField("script", StructType(Array(
            StructField("asm", DataTypes.StringType, true),
            StructField("hex", DataTypes.StringType, true),
            StructField("reqsigs", DataTypes.StringType, true)

          )), true),
        ))), true))), true)))


    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")
    val inputDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "bitcoin-transactions")
      .option("startingoffsets" , "latest")
      .option("failOnDataLoss", "false")
      .load()



    var transactionKey = inputDf.selectExpr("CAST(key AS STRING)")
   var transacationValue = inputDf.selectExpr("CAST(value AS STRING)")

   var transactionData = transacationValue.select(from_json($"value", schema).alias("transactionData"))


    var avgConfirmations = transactionData.withColumn("timestamp", to_timestamp($"transactionData.payload.time", "yyyy-MM-dd HH:mm:ss"))
      .groupBy($"timestamp")
      .agg(sum("transactionData.payload.confirmations"))



    val writeStream = avgConfirmations.writeStream
      .outputMode("update")
      .format("console")
      .start()
    writeStream.awaitTermination()

    


  }




}
