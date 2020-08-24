package com.dingdo.Component.classifier;

import com.dingdo.enums.ClassicEnum;
import com.dingdo.util.FileUtil;
import com.dingdo.util.NLPUtils;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
//
///**
// * 朴素贝叶斯分类器模块
// * 使用spark.mllib机器学习库实现
// */
//@Component
public class NaiveBayesComponent {
//public class NaiveBayesComponent implements ApplicationRunner {
//
//    // 使用log4j打印日志
//    private static Logger logger = Logger.getLogger(NaiveBayesComponent.class);
//    // 训练数据列表
//    private List<LabeledPoint> train_list = new ArrayList<>();
//    // spark设置
//    private SparkConf sparkConf = null;
//    // javaSpark类
//    private JavaSparkContext sc = null;
//    // JavaRDD
//    private JavaRDD<LabeledPoint> trainingRDD = null;
//    // 贝叶斯分类模块
//    private NaiveBayesModel nb_model = null;
//
//    public static Map<String, Integer> getNewVocabulary() {
//        return newVocabulary;
//    }
//
//    private static Map<String, Integer> newVocabulary = new HashMap<>();
//
//    public NaiveBayesComponent() {
//        newInitVocabulary();
//    }
//
//    /**
//     * 加载词典表
//     */
//    private static void newInitVocabulary() {
//        // 初始化字典表
//        String scoreVocabulary = FileUtil.loadFile("python/CQPython/static/dict/newVocabulary.txt");
//        String[] vocabularies = scoreVocabulary.split("\n");
//        for (int i = 0; i < vocabularies.length; i++) {
//            String[] vocabularyList = vocabularies[i].split(" ");
//            for (String vocabulary : vocabularyList) {
//                newVocabulary.put(vocabulary, i);
//            }
//        }
//    }
//
//    /**
//     * 初始化训练数据
//     */
//    private void initTrainData() {
//        // 训练数据为question/function文件夹下的文件
//        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();
//
//        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
//            String scoreQuestions = FileUtil.loadFile(file.getKey());
//            String[] questions = scoreQuestions.split("\n");
//            ClassicEnum enumByFileName = ClassicEnum.getEnumByFileName(file.getValue());
//
//            double label = 0;
//            if (enumByFileName != null) {
//                label = enumByFileName.getValue();
//            }
//
//            for (String question : questions) {
//                double[] questionVector = newSentenceToArrays(question, null);
//                LabeledPoint trian = new LabeledPoint(label, Vectors.dense(questionVector));
//                train_list.add(trian);
//            }
//            logger.warn(file.getValue() + "加载：" + questions.length);
//        }
//    }
//
//    private void initConfig() {
//        this.sparkConf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
//        this.sc = new JavaSparkContext(sparkConf);
//        /**
//         * 屏蔽spark的INFO日志
//         */
//        Logger.getLogger("org").setLevel(Level.WARN);
//        Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
//        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.WARN);
//        SparkSession.builder().getOrCreate().sparkContext().setLogLevel("WARN");
//        /**
//         * SPARK的核心是RDD(弹性分布式数据集)
//         * Spark是Scala写的,JavaRDD就是Spark为Java写的一套API
//         * JavaSparkContext sc = new JavaSparkContext(sparkConf);    //对应JavaRDD
//         * SparkContext	    sc = new SparkContext(sparkConf)    ;    //对应RDD
//         * 数据类型为LabeledPoint
//         */
//        this.trainingRDD = sc.parallelize(train_list);
//        /**
//         * 利用Spark进行数据分析时，数据一般要转化为RDD
//         * JavaRDD转Spark的RDD
//         */
//        this.nb_model = NaiveBayes.train(trainingRDD.rdd());
//    }
//
//    /**
//     * 朴素贝叶斯分类器确定自然语言语义
//     * @param msg
//     * @return
//     */
//    public double predict(String msg) {
//        // 测试数据
//        double[] dTest = newSentenceToArrays(msg, NLPUtils::queryAbstract);
//        Vector vTest = Vectors.dense(dTest);//测试对象为单个vector，或者是RDD化后的vector
//
//        System.out.println(nb_model.predictProbabilities(vTest));
//        // 计算测试目标向量与训练样本数据集里面对应的各个分类标签匹配的概率结果
//        return nb_model.predict(vTest);
//    }
//
//
//
//    /**
//     * 根据词典将训练数据初始化为向量
//     *
//     * @param sentence
//     * @param toAbstractFunction 将句子抽象化的方法
//     * @return
//     */
//    public static double[] newSentenceToArrays(String sentence, BiFunction<String, String[],
//                                                    List<Term>> toAbstractFunction) {
//        Integer max = newVocabulary.values().stream().distinct().max(Integer::compareTo).get();
//        double[] vector = new double[max];
//
//        // 使用hanlp进行分词，将分词的结果对比词典表组成单词向量
//        Segment segment = NLPUtils.getNativeSegment();
//        List<Term> terms = null;
//        if (toAbstractFunction != null) {
//            // 如果传入了抽象化方法，则将分词结果抽象化
//            terms = toAbstractFunction.apply(sentence, new String[]{"ns"});
//        } else {
//            terms = segment.seg(sentence);
//        }
//
//        // 特征向量提取
//        for (Term term : terms) {
//            String word = term.word;
//            Integer vectorIndex = newVocabulary.get(word);
//            if (vectorIndex != null) {
//                try {
//                    vector[vectorIndex] = 1;
//                } catch (Exception e) {
//                    System.out.println();
//                }
//            }
//        }
//        return vector;
//    }
//
//
//    /**
//     * 在程序运行时便会执行的方法
//     * 这里进行组件初始化
//     * @param args
//     * @throws Exception
//     */
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
////        this.initVocabulary();
//        this.newInitVocabulary();
//        logger.warn("朴素贝叶斯分类器加载词典完毕");
//        this.initTrainData();
//        logger.warn("朴素贝叶斯分类器加载训练数据完毕");
//        this.initConfig();
//        logger.warn("朴素贝叶斯分类器初始化完毕");
//    }
//
//
//    /**
//     * 服务关闭时释放资源
//     */
//    @PreDestroy
//    public void destory() {
//        //最后不要忘了释放资源
//        sc.close();
//    }
}
