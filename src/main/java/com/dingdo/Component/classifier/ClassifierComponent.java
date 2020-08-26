package com.dingdo.Component.classifier;

import org.apache.log4j.Level;
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

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ClassifierComponent<Classifier extends ProbabilisticClassifier, Model extends ProbabilisticClassificationModel> {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(ClassifierComponent.class);
    // 分类器名称
    protected String classifierName = "defaultClassifier";
    // sparkSession
    protected SparkSession spark = null;
    // 预测索引与预测标签Map
    protected Map<Object, Object> predictedLabelMap;
    // 分类的模型
    protected Model model = null;
    // 模型管道
    private PipelineModel pipelineModel = null;

    public ClassifierComponent() {
        initSpark();
    }

    public ClassifierComponent(String classifierName) {
        this();
        this.classifierName = classifierName;
    }

    /**
     * 初始化SparkSession
     *
     * @return
     */
    private void initSpark() {
        // 如果还没有初始化spark，创建sparkSession
        this.spark = SparkSession
                .builder()
                .appName(this.classifierName)
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file///:G:/Projects/Java/Spark/spark-warehouse")
                .getOrCreate();

        // 屏蔽spark的INFO日志
        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
        Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR);
        Logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);
        Logger.getLogger("org.apache.hive").setLevel(Level.WARN);
        SparkSession.builder().getOrCreate().sparkContext().setLogLevel("WARN");
    }


    /**
     * 加载模型
     *
     * @param path 模型路径
     * @return
     */
    public Model load(String path, Function<String, Model> loadFunction) {
        return (Model) loadFunction.apply(path);
    }


    /**
     * 将文件提取为Dataset<Row>
     *
     * @param filePath 文件路径
     * @param format   文件数据格式
     * @return
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
     * @throws InstantiationException   Classifier实例化失败
     * @throws IllegalAccessException   非法访问异常
     */
    public void fit(String trainDataPath, String dataFormat, Class<Classifier> classifierClass) throws IllegalAccessException, InstantiationException {
        this.fit(this.getDataFromFileByFormat(trainDataPath, dataFormat), classifierClass);
    }


    /**
     * 训练模型
     *
     * @param trainData       训练数据集
     * @param classifierClass 训练的模型类型
     * @throws InstantiationException   Classifier实例化失败
     * @throws IllegalAccessException   非法访问异常
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
     * @return  预测的分类标签
     */
    public double evaluate(String testDataPath, String dataFormat) {
        return this.evaluate(this.getDataFromFileByFormat(testDataPath, dataFormat));
    }

    /**
     * 测试模型
     *
     * @param testData 测试数据集
     * @return  预测的分类标签
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
     * @return  预测的分类标签
     */
    public abstract double predict(Object object);


    /**
     * 预测分类
     *
     * @param vectors 待预测分类的向量数组
     * @return  预测的分类标签
     */
    public double predict(double[] vectors) {
        Vector predictVector = Vectors.dense(vectors);
        return this.predict(predictVector);
    }


    /**
     * 预测分类
     *
     * @param predictVector 待预测分类的向量
     * @return  预测的分类标签
     */
    public double predict(Vector predictVector) {
        double predict = model.predict(predictVector);
        return Double.valueOf((String) predictedLabelMap.get(predict));
    }

    private Classifier getInstanceOfT(Class<Classifier> aClass) throws IllegalAccessException, InstantiationException {
        return aClass.newInstance();
    }

    public String getClassifierName() {
        return classifierName;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    protected Model getModel() {
        return model;
    }

    @PreDestroy
    public void destroy() {
        spark.stop();
    }
}
