package com.example.old.component.classifier;

import org.apache.log4j.Logger;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.ProbabilisticClassificationModel;
import org.apache.spark.ml.classification.ProbabilisticClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 一个抽象化的基于spark{@code ProbabilisticClassifier}的分类器，
 * 该分类器集成了load,save,fit,predict,evaluate等方法，将构造一个简单的
 * 语义分类器的过程抽取出来作为单独的父类，继承本类后按照需求构建分类器与
 * 训练的数据即可完成分类模型的构建
 *
 * @param <Classifier>  分类器类型
 * @param <Model>   分类器模型
 * @see ProbabilisticClassifier
 * @see ProbabilisticClassificationModel
 */
public abstract class ClassifierComponent<Classifier extends ProbabilisticClassifier, Model extends ProbabilisticClassificationModel> {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(ClassifierComponent.class);
    // sparkSession
    @Autowired
    protected SparkSession spark;
    // 预测索引与预测标签Map
    protected Map<Double, Object> predictedLabelMap;
    // 分类的模型
    protected Model model = null;
    // 模型管道
    private PipelineModel pipelineModel = null;

    protected ClassifierComponent() {
    }

    /**
     * 加载模型
     * @param path  模型的路径
     * @param loadFunction  具体的模型加载方法
     * @return  加载完成的模型
     */
    public Model load(String path, Function<String, Model> loadFunction) {
        return (Model) loadFunction.apply(path);
    }


    /**
     * 将文件提取为Dataset<Row>
     *
     * @param filePath 文件路径
     * @param format   文件数据格式
     * @return  Dataset数据集
     */
    public Dataset<Row> getDataFromFileByFormat(String filePath, String format) {
        return this.spark.read().format(format).load(filePath);
    }


    /**
     * 训练模型
     * 加载并解析数据文件，然后将其转换为DataFrame。
     *
     * @param trainDataPath   训练数据的地址
     * @param classifierClass 训练的模型类型
     * @throws InstantiationException Classifier实例化失败
     * @throws IllegalAccessException 非法访问异常
     */
    public void fit(String trainDataPath, String dataFormat, Class<Classifier> classifierClass) throws IllegalAccessException, InstantiationException {
        this.fit(this.getDataFromFileByFormat(trainDataPath, dataFormat), classifierClass);
    }


    /**
     * 训练模型
     *
     * @param trainData       训练数据集
     * @param classifierClass 训练的模型类型
     * @throws InstantiationException Classifier实例化失败
     * @throws IllegalAccessException 非法访问异常
     */
    public void fit(Dataset<Row> trainData, Class<Classifier> classifierClass) throws InstantiationException, IllegalAccessException {

        // 索引标签，将元数据添加到标签列。
        // 适合整个数据集以将所有标签包括在索引中。
        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("label")
                .setOutputCol("indexedLabel")
                .fit(trainData);

        // 自动识别分类特征并为其编制索引。
        // 设置maxCategories，以便具有> 4个不同值的要素被视为连续要素。
        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(4)
                .fit(trainData);

        // 构建分类模型。
        Classifier classifier = null;

        classifier = (Classifier) this.getInstanceOfT(classifierClass)
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures");

        // 将索引标签转换回原始标签。
        IndexToString labelConverter = new IndexToString()
                .setInputCol("prediction")
                .setOutputCol("predictedLabel")
                .setLabels(labelIndexer.labelsArray()[0]);

        // 管道中的链索引器
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, classifier, labelConverter});

        // 训练模型。这也会运行索引器。
        this.pipelineModel = pipeline.fit(trainData);
        pipelineModel.transform(trainData)
                .select("label", "features", "indexedLabel", "prediction", "predictedLabel")
                .show(5);

        // 将预测索引与标签索引组装为map
        predictedLabelMap = pipelineModel.transform(trainData)
                .select("prediction", "predictedLabel")
                .distinct()
                .collectAsList()
                .stream()
                .collect(Collectors.toMap(item -> item.getAs("prediction"), item -> item.getAs("predictedLabel")));

        this.model = (Model) (pipelineModel.stages()[2]);
    }

    /**
     * 测试模型
     *
     * @param testDataPath 测试数据地址
     * @param dataFormat   测试数据的格式
     * @return 预测的分类标签
     */
    public double evaluate(String testDataPath, String dataFormat) {
        return this.evaluate(this.getDataFromFileByFormat(testDataPath, dataFormat));
    }

    /**
     * 测试模型
     *
     * @param testData 测试数据集
     * @return 预测的分类标签
     */
    public double evaluate(Dataset<Row> testData) {

        Dataset<Row> predictions = pipelineModel.transform(testData);
        predictions.select("predictedLabel", "label", "features").show(5);

        // 测试模型
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");

        return evaluator.evaluate(predictions);
    }

    /**
     * 预测分类
     *
     * @param object 待预测分类的项
     * @return 预测的分类标签
     */
    public abstract double predict(Object object);


    /**
     * 预测分类
     *
     * @param vectors 待预测分类的向量数组
     * @return 预测的分类标签
     */
    public double predict(double[] vectors) {
        Vector predictVector = Vectors.dense(vectors);
        return this.predict(predictVector);
    }


    /**
     * 预测分类
     *
     * @param predictVector 待预测分类的向量
     * @return 预测的分类标签
     */
    public double predict(Vector predictVector) {
        double predict = model.predict(predictVector);
        return Double.valueOf((String) predictedLabelMap.get(predict));
    }

    private Classifier getInstanceOfT(Class<Classifier> aClass) throws IllegalAccessException, InstantiationException {
        return aClass.newInstance();
    }

    protected Model getModel() {
        return model;
    }

    @PreDestroy
    public void destroy() {
        spark.stop();
    }
}
