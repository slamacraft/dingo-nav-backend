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

import java.util.function.Function;

public abstract class ClassifierComponent<Classifier extends ProbabilisticClassifier, Model extends ProbabilisticClassificationModel> {

    // ==============================================静态成员变量===============================================
    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(ClassifierComponent.class);

    // ================================================私有成员变量=============================================
    // 分类器名称
    protected String classifierName = "defaultClassifier";
    // sparkSession
    protected SparkSession spark = null;
    // 分类的模型
    private Model model = null;

    private PipelineModel pipelineModel = null;

    public ClassifierComponent() {
        initSpark();
    }

    public ClassifierComponent(String classifierName) {
        this();
        this.classifierName = classifierName;
    }
    /**
     * 初始化分类模型
     *
     * @return
     */
    private void initSpark() {
        // 创建sparkSession
        this.spark = SparkSession
                .builder()
                .appName(this.classifierName)
                .master("local[*]")
                .config("spark.sql.warehouse.dir",
                        "file///:G:/Projects/Java/Spark/spark-warehouse")
                .getOrCreate();

        // 屏蔽spark的INFO日志
        logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
        logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR);
        logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);
        logger.getLogger("org.apache.hive").setLevel(Level.WARN);
        SparkSession.builder().getOrCreate().sparkContext().setLogLevel("WARN");
    }

    /**
     * 加载随机森林模型
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
     *
     * @param trainDataPath   训练数据的地址
     * @param classifierClass 训练的模型类型
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void fit(String trainDataPath, String dataFormat, Class<Classifier> classifierClass) throws IllegalAccessException, InstantiationException {
        // 加载并解析数据文件，然后将其转换为DataFrame。
        this.fit(this.getDataFromFileByFormat(trainDataPath, dataFormat), classifierClass);
    }

    /**
     * 训练模型
     *
     * @param trainData       训练数据集
     * @param classifierClass 训练的模型类型
     * @throws InstantiationException
     * @throws IllegalAccessException
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
                .setMaxCategories(8)
                .fit(trainData);

        // 训练RandomForest模型。
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
        pipelineModel.transform(trainData).select("predictedLabel", "label", "features").show(5);

        this.model = (Model) (pipelineModel.stages()[2]);
    }

    /**
     * 测试模型
     *
     * @param testDataPath 测试数据地址
     * @param dataFormat   测试数据的格式
     * @return
     */
    public double evaluate(String testDataPath, String dataFormat) {
        return this.evaluate(this.getDataFromFileByFormat(testDataPath, dataFormat));
    }

    /**
     * 测试模型
     *
     * @param testData 测试数据集
     * @return
     */
    public double evaluate(Dataset<Row> testData) {

        Dataset<Row> predictions = pipelineModel.transform(testData);
        predictions.select("predictedLabel", "label", "features").show(5);

        // 测试模型
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");

        double accuracy = evaluator.evaluate(predictions);
        return accuracy;
    }

    /**
     * 预测分类
     *
     * @param object 待预测分类的项
     * @return
     */
    public abstract double predict(Object object);


    /**
     * 预测分类
     *
     * @param vectors 待预测分类的向量数组
     * @return
     */
    public double predict(double[] vectors) {
        Vector predictVector = Vectors.dense(vectors);
        return this.predict(predictVector);
    }

    /**
     * 预测分类
     *
     * @param predictVector 待预测分类的向量
     * @return
     */
    public double predict(Vector predictVector) {
        double predict = model.predict(predictVector);
        Vector vector = model.predictRaw(predictVector);
        return predict;
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
}
