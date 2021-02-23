package com.dingdo.componets.spark.classifier

import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NaiveByesClassifier {

//  @Autowired
  private var spark: SparkSession = _

  def init(): Unit ={
    val trainDataSrc = "D:/workspace/springboot-webjar/src/main/resources/static/question/TrainDataLibSVM.txt"
    val trainData = spark.read.format("libsvm").load(trainDataSrc)

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
}
