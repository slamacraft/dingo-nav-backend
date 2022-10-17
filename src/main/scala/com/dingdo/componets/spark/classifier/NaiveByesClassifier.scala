package com.dingdo.componets.spark.classifier

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.springframework.beans.factory.annotation.Autowired

//@Component
class NaiveByesClassifier {

  @Autowired
  private var spark: SparkSession = _

  val trainDataSrc = "D:/workspace/springboot-webjar/src/main/resources/static/question/TrainDataLibSVM.txt"
  val trainData: DataFrame = spark.read.format("libsvm").load(trainDataSrc)

  //    val Array(train, test) = trainData.randomSplit(Array(0.6, 0.4))
  //
  //    trainData.show(10)
  //
  //    val model = new NaiveBayes().fit(train)
  //    val predictions = model.transform(test)
  //
  //    predictions.show()
  //
  //    val evaluator = new MulticlassClassificationEvaluator()
  //      .setLabelCol("label")
  //      .setPredictionCol("prediction")
  //      .setMetricName("accuracy")
  //
  //    val accuracy = evaluator.evaluate(predictions)
  //    println("准确率：" + accuracy)
}
