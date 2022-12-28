package com.dingo.component.classifier;//package com.dingdo.component;
//
//import com.dingdo.enums.ClassicEnum;
//import com.dingdo.util.FileUtil;
//import com.dingdo.util.nlp.NLPUtils;
//import com.hankcs.hanlp.seg.Segment;
//import com.hankcs.hanlp.seg.common.Term;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.spark.ml.Pipeline;
//import org.apache.spark.ml.PipelineModel;
//import org.apache.spark.ml.PipelineStage;
//import org.apache.spark.ml.classification.RandomForestClassificationModel;
//import org.apache.spark.ml.classification.RandomForestClassifier;
//import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
//import org.apache.spark.ml.feature.*;
//import org.apache.spark.ml.linalg.Vector;
//import org.apache.spark.ml.linalg.Vectors;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//@component
//public class RandomForestComponent implements ApplicationRunner {
//    // 使用log4j打印日志
//    private static Logger logger = Logger.getLogger(RandomForestComponent.class);
//
//    private static Map<String, Integer> vocabulary = new HashMap<>();
//
//    private static Map<Double, Double> labelMap = new HashMap<>();
//
//    private SparkSession spark;
//
//    private RandomForestClassificationModel model;
//
//    /**
//     * 加载词典表
//     */
//    private void initVocabulary() {
//        // 初始化字典表
//        String scoreVocabulary = FileUtil.loadFile("python/CQPython/static/dict/newVoacbulary.txt");
//        String[] vocabularies = scoreVocabulary.split("\n");
//        for (int i = 0; i < vocabularies.length; i++) {
//            String[] vocabularyList = vocabularies[i].split(" ");
//            for (String vocabulary : vocabularyList) {
//                RandomForestComponent.vocabulary.put(vocabulary, i);
//            }
//        }
//    }
//
//    private void indexToLabel() {
//        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();
//        double index = 0;
//        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
//            String scoreQuestions = FileUtil.loadFile(file.getKey());
//            if (StringUtils.isBlank(scoreQuestions)) {
//                continue;
//            }
//            double label = 0;
//            ClassicEnum enumByFileName = ClassicEnum.getEnumByFileName(file.getValue());
//            if (enumByFileName != null) {
//                label = enumByFileName.getValue();
//            }
//            labelMap.put(index, label);
//            index += 1;
//        }
//    }
//
//    /**
//     * 初始化训练数据
//     */
//    private void initTrainData() {
//        // 训练数据为question/function文件夹下的文件
//        FileUtil.clearFile("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\TrainDataLibSVM.txt");
//        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();
//
//        double index = 0;
//        StringBuffer toWriteString = new StringBuffer();
//        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
//            String scoreQuestions = FileUtil.loadFile(file.getKey());
//            if (StringUtils.isBlank(scoreQuestions)) {
//                continue;
//            }
//            String[] questions = scoreQuestions.split("\n");
//            for (String question : questions) {
//                double[] questionVector = sentenceToArrays(question, null);
//                toWriteString.append("你好"+index);
//
//                for (int i = 0; i < questionVector.length; i++) {
//                    toWriteString.append(" " + (i + 1) + ":" + questionVector[i]);
//                }
//                toWriteString.append("\n");
//            }
//            index += 1;
//            logger.warn(file.getValue() + "加载：" + questions.length);
//        }
//
//        FileUtil.writeFile("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\TrainDataLibSVM.txt"
//                , toWriteString.toString());
//    }
//
//    /**
//     * 根据词典将训练数据初始化为向量
//     *
//     * @param sentence
//     * @param toAbstractFunction 将句子抽象化的方法
//     * @return
//     */
//    public static double[] sentenceToArrays(String sentence, Function<String, List<Term>> toAbstractFunction) {
//        Integer max = vocabulary.values().stream().distinct().max(Integer::compareTo).get();
//        double[] vector = new double[max];
//
//        // 使用hanlp进行分词，将分词的结果对比词典表组成单词向量
//        Segment segment = NLPUtils.getNativeSegment();
//        List<Term> terms = null;
//        if (toAbstractFunction != null) {
//            // 如果传入了抽象化方法，则将分词结果抽象化
//            terms = toAbstractFunction.apply(sentence);
//        } else {
//            terms = segment.seg(sentence);
//        }
//
//        // 特征向量提取
//        for (Term term : terms) {
//            String word = term.word;
//            Integer vectorIndex = vocabulary.get(word);
//            if (vectorIndex != null) {
//                try {
//                    vector[vectorIndex] += 1;
//                } catch (Exception e) {
//                    System.out.println();
//                }
//            }
//        }
//        return vector;
//    }
//
//    /**
//     * 加载随机森林模型
//     *
//     * @param path 模型路径
//     * @return
//     */
//    public RandomForestClassificationModel loadRFModel(String path) {
//        return RandomForestClassificationModel.load(path);
//    }
//
//    public RandomForestClassificationModel getAndTrainRFModel(String trainDataPath) {
//        // 加载并解析数据文件，然后将其转换为DataFrame。
//        Dataset<Row> data = spark.read().format("libsvm").load(trainDataPath);
//
//        //索引标签，将元数据添加到标签列。
//        // 适合整个数据集以将所有标签包括在索引中。
//        StringIndexerModel labelIndexer = new StringIndexer()
//                .setInputCol("label")
//                .setOutputCol("indexedLabel")
//                .fit(data);
//        //自动识别分类特征并为其编制索引。
//        // 设置maxCategories，以便具有> 4个不同值的要素被视为连续要素。
//        VectorIndexerModel featureIndexer = new VectorIndexer()
//                .setInputCol("features")
//                .setOutputCol("indexedFeatures")
//                .setMaxCategories(4)
//                .fit(data);
//
//        //将数据分为训练集和测试集（保留30％进行测试）
//        Dataset<Row>[] splits = data.randomSplit(new double[]{0.7, 0.3});
//        Dataset<Row> trainingData = splits[0];
//        Dataset<Row> testData = splits[1];
//
//        //训练RandomForest模型。
//        RandomForestClassifier rf = new RandomForestClassifier()
//                .setLabelCol("indexedLabel")
//                .setFeaturesCol("indexedFeatures");
//
//        //将索引标签转换回原始标签。
//        IndexToString labelConverter = new IndexToString()
//                .setInputCol("prediction")
//                .setOutputCol("predictedLabel")
//                .setLabels(labelIndexer.labels());
//
//        System.out.println(labelIndexer.labelsArray());
//
//        //管道中的链索引器和林
//        Pipeline pipeline = new Pipeline()
//                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, rf, labelConverter});
//
//        //训练模型。这也会运行索引器。
//        PipelineModel pipelineModel = pipeline.fit(trainingData);
//
//        // 作出预测。
//        Dataset<Row> predictions = pipelineModel.transform(testData);
//
//        logger.warn("=================================================================================");
//        //选择要显示的示例行。
//        predictions.select("indexedLabel", "predictedLabel", "label", "features").show(5);
//        logger.warn("=================================================================================");
//        //选择（预测，真实标签）并计算测试错误
//        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
//                .setLabelCol("indexedLabel")
//                .setPredictionCol("prediction")
//                .setMetricName("accuracy");
//        double accuracy = evaluator.evaluate(predictions);
//        System.out.println("Test Error = " + (1.0 - accuracy));
//
//        RandomForestClassificationModel rfModel = (RandomForestClassificationModel) (pipelineModel.stages()[2]);
//        System.out.println("Learned classification forest model:\n" + rfModel.toDebugString());
//
//        return rfModel;
//    }
//
//    /**
//     * 初始化随机森林模型
//     *
//     * @return
//     */
//    public void initSpark() {
//        spark = SparkSession
//                .builder()
//                .appName("RandomForest")
//                .master("local[*]")
//                .config("spark.sql.warehouse.dir",
//                        "file///:G:/Projects/Java/Spark/spark-warehouse")
//                .getOrCreate();
//
//        /**
//         * 屏蔽spark的INFO日志
//         */
//        logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
//        logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR);
//        logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);
//        logger.getLogger("org.apache.hive").setLevel(Level.WARN);
//        SparkSession.builder().getOrCreate().sparkContext().setLogLevel("WARN");
//    }
//
//    public static void main(String args[]) throws IOException {
//        RandomForestComponent randomForestComponent = new RandomForestComponent();
//        randomForestComponent.initVocabulary();
//        randomForestComponent.initTrainData();
////        randomForestComponent.initSpark();
////        randomForestComponent.indexToLabel();
//////        RandomForestClassificationModel model = randomForestComponent.getAndTrainRFModel("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\TrainDataLibSVM.txt");
////        RandomForestClassificationModel model = RandomForestClassificationModel.load("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\model");
////        double[] vectors = RandomForestComponent.sentenceToArrays("今天温度多少", null);
////        Vector dense = Vectors.dense(vectors);
////        Vector vector1 = model.predictRaw(dense);
////        Vector vector2 = model.predictLeaf(dense);
////        double predict = model.predict(dense);
////        logger.warn(vector1);
////        logger.warn(vector2);
////        logger.warn(predict);
//////        model.write().overwrite().save("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\model");
////        System.out.println();
//    }
//
//    public double predict(String msg) {
//        double[] vectors = RandomForestComponent.sentenceToArrays(msg, null);
//        Vector dense = Vectors.dense(vectors);
//        double predict = model.predict(dense);
//        Vector vector = model.predictRaw(dense);
//        logger.warn(vector);
//        logger.warn(predict);
//        return this.labelMap.get(predict);
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        initSpark();
//        logger.warn("随机森林组件初始化spark完毕");
//        initVocabulary();
//        logger.warn("随机森林组件初始化词典完毕");
////        initTrainData();
//        indexToLabel();
//        logger.warn("随机森林组件初始化label字典完毕");
//        this.model = this.loadRFModel("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\model");
////        this.model = this.getAndTrainRFModel("D:\\workspace\\Graduation-project\\src\\main\\resources\\python\\CQPython\\static\\question\\TrainDataLibSVM.txt");
//        logger.warn("随机森林组件初始化模组完毕");
//    }
//}
