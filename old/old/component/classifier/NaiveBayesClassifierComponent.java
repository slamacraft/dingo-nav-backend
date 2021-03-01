package com.example.old.component.classifier;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.common.exception.ClassifierInitializeException;
import com.dingdo.enums.ClassicEnum;
import com.dingdo.util.FileUtil;
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
    private static Logger logger = Logger.getLogger(NaiveBayesClassifierComponent.class);

    // 词典路径
    private String vocabularyPath;
    // 训练数据路径
    private String trainDataPath;
    // 模型保存路径
    private String modelSavePath;
    // 模型加载路径
    private String modelLoadPath;
    // 词典集
    private Map<String, Integer> vocabulary = new HashMap<>();

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
    public void run() throws IOException, InstantiationException, IllegalAccessException {
        if (!new File(vocabularyPath).exists()) {
            throw new ClassifierInitializeException("分类器初始化异常，文件" + vocabularyPath + "不存在");
        }

        initVocabulary();

        if (StringUtils.isNotBlank(modelLoadPath)) {    // 如果模型加载路径不为空，则优先加载模型
            try {
                this.load(modelLoadPath);
            } catch (Exception e) {
                retrainModel();
            }
        }

        logger.info("朴素贝叶斯模型初始化完成");
    }


    /**
     * 从文件路径加载模型权重
     *
     * @param path 模型的路径
     */
    public void load(String path) {
        String label = FileUtil.loadFile(modelSavePath + "/label.txt");
        JSONObject jsonObject = JSONObject.parseObject(label);

        super.predictedLabelMap = ((Map<String, Object>) jsonObject).entrySet().stream()
                .collect(Collectors.toMap(item -> Double.valueOf(item.getKey()), Map.Entry::getValue));

        this.model = super.load(path, NaiveBayesModel::load);
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
        if (!trainDataFile.exists() || StringUtils.isBlank(FileUtil.loadFile(trainDataPath))) {
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

        FileUtil.writeFile(modelSavePath + "/label.txt", new JSONObject(toSaveLabelMap).toJSONString());
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
     *
     * @param trainDataPath 训练数据路径
     */
    private void initTrainData(String trainDataPath) {
        FileUtil.clearFile(trainDataPath);

        Map<String, String> filePath2NameMap = ClassicEnum.getAllFileSrc();

        StringBuilder toWriteString = new StringBuilder();
        for (Map.Entry<String, String> file : filePath2NameMap.entrySet()) {
            String trainDataFile = FileUtil.loadFileFromResourse(file.getKey());
            if (StringUtils.isBlank(trainDataFile)) {  // 不计入空文件
                continue;
            }

            // 按换行符加载语料
            // 将语料依照词典表转换为libsvm格式文件
            // for example: label key1:value1, key2:value2, key3:value3
            //              1.0   1:0.0      , 2:1.0      , 3:2.0
            double label = 0;
            ClassicEnum enumByFileName = ClassicEnum.getEnumByFileName(file.getValue());
            if (enumByFileName != null) {
                label = enumByFileName.getValue();
            }

            String[] trainDataList = trainDataFile.split("\n");
            for (String trainData : trainDataList) {
                double[] questionVector = sentenceToArrays(trainData, null);

                // 忽视0向量
                double sum = Arrays.stream(questionVector).sum();
                if (sum <= 0) {
                    continue;
                }

                // 将向量构建成稀疏矩阵
                toWriteString.append(label);
                for (int i = 0; i < questionVector.length; i++) {
                    if (questionVector[i] == 0) {
                        continue;
                    }
                    toWriteString.append(" ").append(i + 1).append(":").append(questionVector[i]);
                }
                toWriteString.append("\n");
            }
        }

        try {
            FileUtil.writeFile(trainDataPath, toWriteString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化词典表
     */
    private void initVocabulary() {
        // 初始化字典表
        String scoreVocabulary = FileUtil.loadFile(vocabularyPath);
        String[] vocabularies = scoreVocabulary.split("\n");

        for (int i = 0; i < vocabularies.length; i++) {
            String[] vocabularyList = vocabularies[i].split(" ");
            for (String vocabulary : vocabularyList) {
                this.vocabulary.put(vocabulary, i);
            }
        }
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
        Integer max = vocabulary.values().stream().distinct().max(Integer::compareTo).get();
        double[] vector = new double[max];

        // 进行分词,如果传入了抽象化方法，则将分词结果抽象化
        Segment segment = NLPUtils.getNativeSegment();
        List<Term> terms = null;
        if (toAbstractFunction != null) {
            terms = toAbstractFunction.apply(sentence);
        } else {
            terms = segment.seg(sentence);
        }

        // 特征向量构建
        for (Term term : terms) {
            Integer vectorIndex = vocabulary.get(term.word);
            if ((vectorIndex) != null && vectorIndex < max) {
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
