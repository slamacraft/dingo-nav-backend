package com.example.demo.Component.classifier;//package com.example.demo.Component;
//
//import com.example.demo.common.exception.ClassifierInitializeException;
//import com.example.demo.enums.ClassicEnum;
//import com.example.demo.util.FileUtil;
//import com.example.demo.util.NLPUtils;
//import com.hankcs.hanlp.seg.Segment;
//import com.hankcs.hanlp.seg.common.Term;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
//import org.apache.spark.ml.classification.NaiveBayes;
//import org.apache.spark.ml.classification.NaiveBayesModel;
//import org.apache.spark.ml.linalg.Vectors;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//@Component
//public class NaiveBayesClassifierComponent
//        extends ClassifierComponent<NaiveBayes, NaiveBayesModel>
//        implements ApplicationRunner {
//
//    // 使用log4j打印日志
//    private static Logger logger = Logger.getLogger(NaiveBayesClassifierComponent.class);
//
//    // 词典路径
//    @Value("${config.classifier.vocabularyPath}")
//    private String vocabularyPath;
//    // 训练数据路径
//    @Value("${config.classifier.trainDataPath}")
//    private String trainDataPath;
//    // 模型保存路径
//    @Value("${config.classifier.modelSavePath}")
//    private String modelSavePath;
//    // 模型加载路径
//    @Value("${config.classifier.modelLoadPath}")
//    private String modelLoadPath;
//
//    // 词典集
//    protected Map<String, Integer> vocabulary = new HashMap<>();
//    // index对应的label映射
//    protected Map<Double, Double> labelMap = new HashMap<>();
//
//    // ================================================指令控制变量=============================================
//    // 是否打印分类详情
//    private boolean enabelPrintInfo = true;
//
//
//    public void test() throws Exception {
//        vocabularyPath = "D:/workspace/Graduation-project/src/main/resources/python/CQPython/static/dict/newVoacbulary.txt";
//        trainDataPath = "D:/workspace/Graduation-project/src/main/resources/python/CQPython/static/question/TrainDataLibSVM.txt";
//        modelSavePath = "D:/workspace/Graduation-project/src/main/resources/python/CQPython/static/question/model";
//        modelLoadPath = "D:/workspace/Graduation-project/src/main/resources/python/CQPython/static/question/model";
//        indexToLabel();
//        if (!new File(vocabularyPath).exists()) {
//            throw new ClassifierInitializeException("分类器初始化异常，文件" + vocabularyPath + "不存在");
//        }
//        initVocabulary();
////        // 如果模型加载路径不为空，则优先加载模型
////        if (StringUtils.isNotBlank(modelLoadPath)) {
////            try {
////                this.load(modelLoadPath);
////                return;
////            }
////            // 模型加载失败，重新准备训练数据进行训练
////            catch (Exception e) {
////                logger.warn("加载模型" + modelLoadPath + "失败" + "，尝试重新训练模型");
////            }
////        }
//        Dataset<Row> data = super.getDataFromFileByFormat(trainDataPath, "libsvm");
//        Dataset<Row>[] splits = data.randomSplit(new double[]{0.7, 0.3});
//
//        File trainDataFile = new File(trainDataPath);
//        if (!trainDataFile.exists()) {
//            trainDataFile.createNewFile();
//        }
//        initTrainData(trainDataPath);
//        fit(splits[0]);
//        saveOrOverwrite(modelSavePath);
//
////        logger.warn("模型正确率：" + evaluate(splits[1]));
////        this.indexToLabel();
//        logger.warn("朴素贝叶斯模型初始化完成");
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        indexToLabel();
//        if (!new File(vocabularyPath).exists()) {
//            throw new ClassifierInitializeException("分类器初始化异常，文件" + vocabularyPath + "不存在");
//        }
//        initVocabulary();
////        // 如果模型加载路径不为空，则优先加载模型
////        if (StringUtils.isNotBlank(modelLoadPath)) {
////            try {
////                this.load(modelLoadPath);
////                return;
////            }
////            // 模型加载失败，重新准备训练数据进行训练
////            catch (Exception e) {
////                logger.warn("加载模型" + modelLoadPath + "失败" + "，尝试重新训练模型");
////            }
////        }
//        Dataset<Row> data = super.getDataFromFileByFormat(trainDataPath, "libsvm");
//        Dataset<Row>[] splits = data.randomSplit(new double[]{0.7, 0.3});
//
//        File trainDataFile = new File(trainDataPath);
//        if (!trainDataFile.exists()) {
//            trainDataFile.createNewFile();
//        }
//        initTrainData(trainDataPath);
//        fit(splits[0]);
//        saveOrOverwrite(modelSavePath);
//
////        logger.warn("模型正确率：" + evaluate(splits[1]));
////        this.indexToLabel();
//        logger.warn("朴素贝叶斯模型初始化完成");
//    }
//
//    public void load(String path) {
//        super.load(path, NaiveBayesModel::load);
//        printInfo(logger::warn, "朴素贝叶斯模型加载完毕");
//    }
//
//    public void save(String path) {
//        try {
//            super.getModel().save(path);
//        } catch (IOException e) {
//            logger.error("朴素贝叶斯模型保存失败", e);
//        }
//        printInfo(logger::warn, "朴素贝叶斯模型加载完毕");
//    }
//
//    public void saveOrOverwrite(String path) {
//        try {
//            super.getModel().write().overwrite().save(path);
//        } catch (IOException e) {
//            logger.error("朴素贝叶斯模型保存失败", e);
//        }
//        printInfo(logger::warn, "朴素贝叶斯模型加载完毕");
//    }
//
//    public void fit(String trainDataPath, String dataFormat) throws InstantiationException, IllegalAccessException {
//        super.fit(trainDataPath, dataFormat, NaiveBayes.class);
//        printInfo(logger::warn, "朴素贝叶斯模型训练完毕");
//    }
//
//    public void fit(Dataset<Row> trainData) throws IllegalAccessException, InstantiationException {
//        super.fit(trainData, NaiveBayes.class);
//        printInfo(logger::warn, "朴素贝叶斯模型训练完毕");
//    }
//
//    @Override
//    public double predict(Object object) {
//        double[] vectors = this.sentenceToArrays((String) object, NaiveBayesClassifierComponent::queryPlaceAbstract);
//        Double label = labelMap.get(super.predict(vectors));
//        printInfo(logger::warn, super.getModel().predictRaw(Vectors.dense(vectors)));
//        printInfo(logger::warn, super.predict(vectors));
//        printInfo(logger::warn, "预测的分类为" + label);
//        return label;
//    }
//
//
//    /**
//     * =================================================下面是初始化方法===============================================
//     */
//
//    /**
//     * 初始化训练数据
//     */
//    private void initTrainData(String trainDataPath) {
//        // 训练数据为question/function文件夹下的文件
//        FileUtil.clearFile(trainDataPath);
//        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();
//
//        double index = 0;
//        StringBuffer toWriteString = new StringBuffer();
//        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
//            String trainDataFile = FileUtil.loadFile(file.getKey());
//            if (StringUtils.isBlank(trainDataFile)) {  // 不计入空文件
//                continue;
//            }
//
//            // 按换行符加载语料
//            // 将语料依照词典表转换为libsvm格式文件
//            // for example: label key1:value1, key2:value2, key3:value3
//            //              1.0   1:0.0      , 2:1.0      , 3:2.0
//            double label = 0;
//            ClassicEnum enumByFileName = ClassicEnum.getEnumByFileName(file.getValue());
//            if (enumByFileName != null) {
//                label = enumByFileName.getValue();
//            }
//            labelMap.put(index, label);
//
//            String[] trainDataList = trainDataFile.split("\n");
//            for (String trainData : trainDataList) {
//                double[] questionVector = sentenceToArrays(trainData, null);
//                double sum = Arrays.stream(questionVector).sum();
//                if (sum <= 0) {
//                    continue;
//                }
//
//                toWriteString.append(label);
//                for (int i = 0; i < questionVector.length; i++) {
////                    if (questionVector[i] > 0)
//                        toWriteString.append(" " + (i + 1) + ":" + questionVector[i]);
//                }
//                toWriteString.append("\n");
//            }
//
//            index += 1;
//            logger.warn(file.getValue() + "加载：" + trainDataList.length);
//        }
//
//        FileUtil.writeFile(trainDataPath, toWriteString.toString());
//    }
//
//    /**
//     * 初始化词典表
//     */
//    private void initVocabulary() {
//        // 初始化字典表
//        String scoreVocabulary = FileUtil.loadFile("python/CQPython/static/dict/newVoacbulary.txt");
//        String[] vocabularies = scoreVocabulary.split("\n");
//        for (int i = 0; i < vocabularies.length; i++) {
//            String[] vocabularyList = vocabularies[i].split(" ");
//            for (String vocabulary : vocabularyList) {
//                this.vocabulary.put(vocabulary, i);
//            }
//        }
//    }
//
//    /**
//     * 将分类器的index与枚举类型的label对应起来
//     */
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
//     * 根据词典将训练数据初始化为向量
//     *
//     * @param sentence
//     * @param toAbstractFunction 将句子抽象化的方法
//     * @return
//     */
//    private double[] sentenceToArrays(String sentence, Function<String, List<Term>> toAbstractFunction) {
//        // 构建一个维度为词典行数的向量vector，表示语句在该词典下的特征向量
//        Integer max = vocabulary.values().stream().distinct().max(Integer::compareTo).get();
//        double[] vector = new double[max];
//
//        // 使用hanlp进行分词
//        Segment segment = NLPUtils.getNativeSegment();
//        List<Term> terms = null;
//        if (toAbstractFunction != null) {
//            // 如果传入了抽象化方法，则将分词结果抽象化
//            terms = toAbstractFunction.apply(sentence);
//        } else {
//            terms = segment.seg(sentence);
//        }
//
//        printInfo(logger::warn, "分词得结果为" + terms);
//        // 特征向量构建
//        for (Term term : terms) {
//            Integer vectorIndex = vocabulary.get(term.word);
//            if ((vectorIndex) != null && vectorIndex < max) {
//                vector[vectorIndex] += 1;
//            }
//        }
//
////        double size = (double) terms.size();
////        Arrays.stream(vector).forEach(item -> item = item / size);
//        return vector;
//    }
//
//    /**
//     * 将句子中的地名抽象化
//     *
//     * @param querySentence
//     * @return
//     */
//    public static List<Term> queryPlaceAbstract(String querySentence) {
//        // 句子抽象化
//        Segment segment = NLPUtils.getPlaceSegment();
//        List<Term> terms = segment.seg(querySentence);
//        for (Term term : terms) {
//            if (term.nature.toString().equals("ns")) {
//                term.word = term.nature.toString();
//            }
//        }
//        return terms;
//    }
//
//    /**
//     * 打印详情
//     *
//     * @param printFunction
//     * @param infoList
//     */
//    public void printInfo(Consumer<String> printFunction, Object... infoList) {
//        if (this.enabelPrintInfo) {    // 未启用打印详情日志的功能
//            Arrays.stream(infoList).forEach(item -> printFunction.accept(item.toString()));
//        }
//    }
//
//}
