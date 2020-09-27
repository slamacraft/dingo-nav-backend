package com.dingdo.component.classifier;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.common.exception.ClassifierInitializeException;
import com.dingdo.enums.ClassicEnum;
import com.dingdo.util.FileUtils;
import com.dingdo.util.nlp.NLPUtils;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NaiveBayesClassifierComponent
        extends ClassifierComponent<NaiveBayes, NaiveBayesModel> {

    // 使用log4j打印日志
    private static final Logger logger = Logger.getLogger(NaiveBayesClassifierComponent.class);

    // 词典路径
    private final String vocabularyPath;
    // 训练数据路径
    private final String trainDataPath;
    // 模型保存路径
    private final String modelSavePath;
    // 模型加载路径
    private final String modelLoadPath;
    // 词典集
    private final Map<String, Integer> vocabulary = new HashMap<>();
    // 词典中的非同意词类别数
    private int vocabularyLayers;

    public NaiveBayesClassifierComponent(String vocabularyPath, String trainDataPath, String modelSavePath, String modelLoadPath) {
        super();
        this.vocabularyPath = vocabularyPath;
        this.trainDataPath = trainDataPath;
        this.modelSavePath = modelSavePath;
        this.modelLoadPath = modelLoadPath;
    }

    @Autowired
    public NaiveBayesClassifierComponent(Environment environment) {
        this(environment.getProperty("config.classifier.vocabularyPath"),
                environment.getProperty("config.classifier.trainDataPath"),
                environment.getProperty("config.classifier.modelSavePath"),
                environment.getProperty("config.classifier.modelLoadPath"));
    }

    @PostConstruct
    public void run() {
        initVocabulary();
        initModel();
        logger.info("朴素贝叶斯模型初始化完成");
    }


    /**
     * 从文件路径加载模型权重
     *
     * @param path 模型的路径
     */
    public void load(String path) {
        String label = FileUtils.loadFile(modelSavePath + "/label.txt");
        JSONObject jsonObject = JSONObject.parseObject(label);

        super.predictedLabelMap = ((Map<String, Object>) jsonObject).entrySet().stream()
                .collect(Collectors.toMap(item -> Double.valueOf(item.getKey()), Map.Entry::getValue));

        this.model = super.load(path, NaiveBayesModel::load);
    }


    /**
     * 初始化模型
     * <p>
     * 从{@code modelLoadPath}路径加载模型，如果加载失败，
     * 则调用{@link #retrainModel}重新训练一个模型
     * </p>
     */
    public void initModel() {
        if (StringUtils.isNotBlank(modelLoadPath)) {    // 如果模型加载路径不为空，则优先加载模型
            try {
                this.load(modelLoadPath);
            } catch (Exception e) {
                try {
                    retrainModel();
                } catch (IOException | InstantiationException | IllegalAccessException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    /**
     * 重新训练一个分类模型
     *
     * @throws IOException            训练文件路径未找到
     * @throws InstantiationException 分类器实例化异常
     * @throws IllegalAccessException 非法访问异常
     */
    public void retrainModel() throws IOException, InstantiationException, IllegalAccessException {
        File trainDataFile = new File(trainDataPath);
        if (!trainDataFile.exists() || StringUtils.isBlank(FileUtils.loadFile(trainDataPath))) {
            trainDataFile.createNewFile();
            initTrainData(trainDataPath);
        }

        Dataset<Row> data = super.getDataFromFileByFormat(trainDataPath, "libsvm");

        fit(data);
        saveOrOverwrite(modelSavePath);

        Map<String, Object> toSaveLabelMap = super.predictedLabelMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        item -> String.valueOf(item.getKey()), Map.Entry::getValue)
                );

        FileUtils.appendText(modelSavePath + "/label.txt", new JSONObject(toSaveLabelMap).toJSONString());
    }


    /**
     * 保存模型到路径
     *
     * @param path 模型路径
     * @see NaiveBayesModel
     */
    public void save(String path) {
        try {
            super.getModel().save(path);
        } catch (IOException e) {
            logger.error("朴素贝叶斯模型保存失败", e);
        }
    }

    /**
     * 保存并覆盖模型到路径
     *
     * @param path 模型路径
     * @see NaiveBayesModel
     */
    public void saveOrOverwrite(String path) {
        try {
            super.getModel().write().overwrite().save(path);
        } catch (IOException e) {
            logger.error("朴素贝叶斯模型保存失败", e);
        }
    }


    /**
     * 训练模型
     *
     * @param trainDataPath 训练数据的路径
     * @param dataFormat    训练数据格式
     * @throws InstantiationException 分类器实例化异常
     * @throws IllegalAccessException 非法访问异常
     */
    public void fit(String trainDataPath, String dataFormat) throws InstantiationException, IllegalAccessException {
        super.fit(trainDataPath, dataFormat, NaiveBayes.class);
    }


    /**
     * 训练模型
     *
     * @param trainData DataFrame形式的训练数据
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException 分类器实例化异常
     */
    public void fit(Dataset<Row> trainData) throws IllegalAccessException, InstantiationException {
        super.fit(trainData, NaiveBayes.class);
    }


    @Override
    public double predict(Object object) {
        double[] vectors = this.sentenceToArrays((String) object, NaiveBayesClassifierComponent::queryPlaceAbstract);
        return super.predict(vectors);
    }


    /*=================================================下面是初始化方法===============================================*/

    /**
     * 训练数据预处理
     * <p>
     * 以每行为标准加载语料
     * 将语料依照词典表转换为libsvm格式文件
     * for example:
     * label    key1:value1,    key2:value2,    key3:value3
     * 1.0      1:0.0      ,    2:1.0      ,    3:2.0
     *
     * @param trainDataPath 训练数据路径
     */
    private void initTrainData(String trainDataPath) {
        FileUtils.clearFile(trainDataPath);

        Map<Double, String> allLayerFileMap = ClassicEnum.getAllLayerFileMap();

        StringBuilder toWriteString = new StringBuilder();

        allLayerFileMap.forEach((key, value) -> {
            // 获取不为空的文件
            String trainDataFile = FileUtils.loadFileFromResource(value);
            if (StringUtils.isBlank(trainDataFile)) {
                return;
            }

            // 将文件按行拆分为句子，并将句子提取为特征向量
            Arrays.stream(trainDataFile.split("\n"))
                    .map(this::sentenceToArrays)
                    .filter(item -> Arrays.stream(item).sum() > 0
                    ).forEach(item -> {
                toWriteString.append(key);
                // 将特征向量保存为稀疏矩阵
                for (int i = 0; i < item.length; i++) {
                    if (item[i] == 0) {
                        continue;
                    }
                    toWriteString.append(" ").append(i + 1).append(":").append(item[i]);
                }
                toWriteString.append("\n");
            });
        });
        FileUtils.appendText(trainDataPath, toWriteString.toString());
//
//        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();
//
//        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
//            String trainDataFile = FileUtils.loadFileFromResource(file.getKey());
//            if (StringUtils.isBlank(trainDataFile)) {  // 不计入空文件
//                continue;
//            }
//
//            double label = 0;
//            ClassicEnum enumByFileName = ClassicEnum.getEnumByFileName(file.getValue());
//            if (enumByFileName != null) {
//                label = enumByFileName.getValue();
//            }
//
//            String[] trainDataList = trainDataFile.split("\n");
//            for (String trainData : trainDataList) {
//                double[] questionVector = sentenceToArrays(trainData, null);
//
//                // 忽视0向量
//                double sum = Arrays.stream(questionVector).sum();
//                if (sum <= 0) {
//                    continue;
//                }
//
//                // 将向量构建成稀疏矩阵
//                toWriteString.append(label);
//                for (int i = 0; i < questionVector.length; i++) {
//                    if (questionVector[i] == 0) {
//                        continue;
//                    }
//                    toWriteString.append(" ").append(i + 1).append(":").append(questionVector[i]);
//                }
//                toWriteString.append("\n");
//            }
//        }
    }

    /**
     * 初始化词典表
     */
    private void initVocabulary() {
        if (!new File(vocabularyPath).exists()) {
            throw new ClassifierInitializeException("分类器初始化异常，文件" + vocabularyPath + "不存在");
        }
        // 初始化字典表
        String scoreVocabulary = FileUtils.loadFile(vocabularyPath);
        String[] vocabularies = scoreVocabulary.split("\n");

        for (int i = 0; i < vocabularies.length; i++) {
            String[] vocabularyList = vocabularies[i].split(" ");
            for (String vocabulary : vocabularyList) {
                this.vocabulary.put(vocabulary, i);
            }
        }

        this.vocabularyLayers = vocabulary.values().stream().distinct().max(Integer::compareTo).get();
    }


    /**
     * 根据词典将语句分解为特征向量
     * <p>
     * 本方法为{@link #sentenceToArrays(String, Function)}方法{@code toAbstractFunction}
     * 默认值为{@code null}的重载入口
     *
     * @param sentence 语句
     * @return 特征向量
     * @see #sentenceToArrays(String, Function)
     */
    private double[] sentenceToArrays(String sentence) {
        return sentenceToArrays(sentence, null);
    }

    /**
     * 根据词典将训练数据初始化为向量
     *
     * @param sentence           语句
     * @param toAbstractFunction 将句子抽象化的方法引用
     * @return 特征向量
     */
    private double[] sentenceToArrays(String sentence, Function<String, List<Term>> toAbstractFunction) {
        // 构建一个维度为词典行数的向量vector，表示语句在该词典下的特征向量
        double[] vector = new double[vocabularyLayers];

        // 进行分词,如果传入了抽象化方法，则将分词结果抽象化
        Segment segment = NLPUtils.getNativeSegment();
        List<Term> terms = toAbstractFunction != null ? toAbstractFunction.apply(sentence) : segment.seg(sentence);

        // 特征向量构建
        for (Term term : terms) {
            Integer vectorIndex = vocabulary.get(term.word);
            if ((vectorIndex) != null && vectorIndex < vocabularyLayers) {
                vector[vectorIndex] += 1;
            }
        }

        return vector;
    }


    /**
     * 将句子中的地名抽象化
     *
     * @param querySentence 语句
     * @return 分词结果集
     */
    public static List<Term> queryPlaceAbstract(String querySentence) {
        // 句子抽象化
        Segment segment = NLPUtils.getPlaceSegment();
        List<Term> terms = segment.seg(querySentence);
        for (Term term : terms) {
            if (term.nature.toString().equals("ns")) {
                term.word = term.nature.toString();
            }
        }
        return terms;
    }

}
