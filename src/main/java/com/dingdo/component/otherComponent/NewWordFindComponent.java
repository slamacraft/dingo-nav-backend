package com.dingdo.component.otherComponent;

import com.dingdo.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.math3.util.FastMath.log;

/**
 * 新词发现组件
 * 基于互信息和邻接熵的大文本新词提取
 */
@Component
public class NewWordFindComponent {

    private int word_length = 6;

    /**
     * 从文件中读取文本并且获得文本的词频表
     *
     * @param path_url 文件路径
     * @return 返回文本的词频表
     * {["word": freq]}
     */
    private Map<String, Long> count_words(String path_url) {
        Map<String, Long> word_freq;

        String text = FileUtil.loadFile(path_url).replaceAll("[\\pP\\pS\\pZ]", "");
        String[] line_text = text.split("\n");

        StringBuffer word = new StringBuffer("");
        List<String> word_list = new LinkedList<>();

        for (int i = 0; i < line_text.length; i++) {
//            char[] words = line_text[i].toCharArray();
            // 逐字切分单词
            for (int j = 0; j < line_text[i].length(); j++) {
                // 清空单词
                word.delete(0, word.length());
                for (int k = j + 1; k <= j + word_length && k <= line_text[i].length(); k++) {
                    word_list.add(line_text[i].substring(j, k));
                }
            }
        }
        // {["word": freq]}
        word_freq = word_list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

//        this.saveNewWord("src/main/python/CQPython/test/txt.txt", word_freq);

        return word_freq;
    }

    /**
     * 筛选所有词频与聚合度度都大于阈值的单词
     *
     * @param word_freq  词频表
     * @param total_word 单词数量
     * @param min_freq   词频阈值
     * @param min_mtro   聚合度阈值
     * @return {["word" : side_freq, freq1, freq2, ...]}
     */
    private List<Map<String, List<Long>>> lrg_info(Map<String, Long> word_freq, int total_word, int min_freq, double min_mtro) {
        Map<String, List<Long>> l_dict = new HashMap<>(); // 左子词典词频表
        Map<String, List<Long>> r_dict = new HashMap<>(); // 右子词典词频表

        for (Map.Entry<String, Long> entry : word_freq.entrySet()) {
            String word = entry.getKey();
            Long freq = entry.getValue();
            if (word.length() < 3) {  //小于等于2个字符的词没必要拆分了
                continue;
            }

            // 切去左/右边的字形成子单词
            String left_word_dict = word.substring(0, word.length() - 1);
            String rigth_word_dict = word.substring(1);

            update_dict(word_freq, l_dict, left_word_dict, freq, total_word, min_freq, min_mtro);
            update_dict(word_freq, r_dict, rigth_word_dict, freq, total_word, min_freq, min_mtro);
        }
        List<Map<String, List<Long>>> dict = new ArrayList<>();
        dict.add(l_dict);
        dict.add(r_dict);

        return dict;
    }


    /**
     * 通过聚合度筛选单词
     *
     * @param word_freq
     * @param side_dict
     * @param side_word
     * @param freq
     * @param total_word
     * @param min_freq
     * @param min_mtro
     */
    private void update_dict(Map<String, Long> word_freq, Map<String, List<Long>> side_dict,
                             String side_word, Long freq, int total_word, int min_freq, double min_mtro) {
        Long side_word_freq = word_freq.get(side_word);

        if (side_word_freq > min_freq) {  // 需要子单词大于阈值才计算聚合度
//                计算子单词的聚合度
//                聚合度 = p(父单词) / ( p(左子单词) * p(右子单词) )
//                       = ( freq(父单词) * 总词数 ) / ( freq(左子单词) * freq(右子单词) )
            double left_mul_info = side_word_freq * total_word / (word_freq.get(side_word.substring(0, side_word.length() - 1)) * word_freq.get(side_word.substring(side_word.length() - 1)));
            double right_mul_info = side_word_freq * total_word / (word_freq.get(side_word.substring(0, 1)) * word_freq.get(side_word.substring(1)));

            double min_mul_info = left_mul_info < right_mul_info ? left_mul_info : right_mul_info;
            if (min_mul_info > min_mtro) {
                List<Long> side_freq_list = side_dict.get(side_word);
                if (CollectionUtils.isNotEmpty(side_freq_list)) {
                    side_freq_list.add(freq);
                } else {
                    side_freq_list = new ArrayList<>();
                    side_freq_list.add(side_word_freq);
                    side_freq_list.add(freq);
                    side_dict.put(side_word, side_freq_list);
                }
            }
        }
    }

    /**
     * 计算子单词的自由度
     *
     * @param side_dict 子单词表
     * @return {["word": entro]}
     */
    private List<Map<String, Double>> cal_entro(List<Map<String, List<Long>>> side_dict) {
        List<Map<String, Double>> entro_side_dict = new ArrayList<>();

        side_dict.forEach(item -> {
            Map<String, List<Long>> entro_rl_dict = item;
            Map<String, Double> word_entro = new HashMap<>();

            for (Map.Entry<String, List<Long>> side_word_freq : entro_rl_dict.entrySet()) {
                String side_word = side_word_freq.getKey();
                List<Long> side_word_freq_list = side_word_freq.getValue();

                double entro = 0;   // 待计算自由度

                side_word_freq_list.remove(0);// 去除第一次出现时的词频
                double sum_side_list = side_word_freq_list.stream().mapToDouble((x) -> x).summaryStatistics().getSum();

                for (Long rm : side_word_freq_list) {
                    // 计算单词的自由度
                    // 自由度 entro = -p*log(p)
                    entro -= rm / sum_side_list * log(2, rm / sum_side_list);
                }
                word_entro.put(side_word, entro);
            }
            entro_side_dict.add(word_entro);
        });

        return entro_side_dict;
    }

    /**
     * @param entro_dict 左右子单词的聚自由度列表
     *                   {[word: entro]}
     * @return 返回能左边成词，右边成词，左右都能成词的单词词频表
     * entro_in_rl_dict:{[word: [l_entro, r_entro]]}
     * entro_in_l_dict:{[word: [l_entro]]}
     * entro_in_r_dict:{[word: [r_entro]]}
     */
    private List<Map<String, List<Double>>> entro_lr_fusion(List<Map<String, Double>> entro_dict) {
        List<Map<String, List<Double>>> entro_in_dict = new ArrayList<>();

        Map<String, Double> entro_r_dict = entro_dict.get(1);
        Map<String, Double> entro_l_dict = entro_dict.get(0);
        HashMap<String, List<Double>> entro_in_rl_dict = new LinkedHashMap<>();
        HashMap<String, List<Double>> entro_in_r_dict = new LinkedHashMap<>();
        HashMap<String, List<Double>> entro_in_l_dict = new LinkedHashMap<>();

        entro_l_dict.forEach((key, value) -> {
            List<Double> list = new ArrayList<>();
            list.add(value);
            entro_in_l_dict.put(key, list);
        });

        entro_r_dict.forEach((word, word_entro) -> {
            if (entro_l_dict.containsKey(word)) {
                List<Double> entro_list = new ArrayList<>();
                entro_list.add(entro_r_dict.get(word));
                entro_list.add(entro_r_dict.get(word));
                entro_in_rl_dict.put(word, entro_list);
                entro_in_l_dict.remove(word);
            } else {
                List<Double> entro_list = new ArrayList<>();
                entro_list.add(entro_r_dict.get(word));
                entro_in_r_dict.put(word, entro_list);
            }
        });

        entro_in_dict.add(entro_in_rl_dict);
        entro_in_dict.add(entro_in_l_dict);
        entro_in_dict.add(entro_in_r_dict);

        return entro_in_dict;
    }

    /**
     * 通过自由度筛选单词
     *
     * @param entro_in_dict
     * @param word_freq
     * @param min_entro
     * @return
     */
    private Map<String, Long> entro_filter(List<Map<String, List<Double>>> entro_in_dict, Map<String, Long> word_freq, double min_entro) {
        Map<String, Long> entro_dict = new LinkedHashMap<>();

        Map<String, List<Double>> entro_in_rl_dict = entro_in_dict.get(0);
        Map<String, List<Double>> entro_in_l_dict = entro_in_dict.get(1);
        Map<String, List<Double>> entro_in_r_dict = entro_in_dict.get(2);

        entro_in_rl_dict.forEach((word, entro_list) -> {
            if (entro_list.get(0) > min_entro && entro_list.get(1) > min_entro) {
                entro_dict.put(word, word_freq.get(word));
            }
        });

        entro_in_l_dict.forEach((word, entro_list) -> {
            if (entro_list.get(0) > min_entro) {
                entro_dict.put(word, word_freq.get(word));
            }
        });

        entro_in_r_dict.forEach((word, entro_list) -> {
            if (entro_list.get(0) > min_entro) {
                entro_dict.put(word, word_freq.get(word));
            }
        });

        return entro_dict;
    }

    /**
     * 按照value值对map进行排序
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    private void toStopWord(Map<String, Long> words, String stopwordPath) {
        String text = FileUtil.loadFile(stopwordPath);
        String[] line_text = text.split("\n");

        StringBuffer word = new StringBuffer("");
        List<String> word_list = new LinkedList<>();

        for (int i = 0; i < line_text.length; i++) {
            words.remove(line_text[i]);
        }
    }

    /**
     * 保存单词
     *
     * @param path
     * @param result
     */
    public void saveNewWord(String path, Map<String, Long> result) {
        //向指定目录输出文件
        FileOutputStream fos = null;
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(path);
            fos = new FileOutputStream(path, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /*
         * 最快的是使用缓冲区的读写入操作
         */
        StringBuffer input_text = new StringBuffer();
        result.forEach((word, freq) -> {
            input_text.append(word + " " + freq.toString() + "\n");
        });

        InputStream inputStrem = new ByteArrayInputStream(input_text.toString().getBytes());
        BufferedInputStream bis = new BufferedInputStream(inputStrem);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        byte[] by = new byte[1024 * 8];
        int num = 0;
        try {
            while ((num = bis.read(by)) != -1) {
                bos.write(by, 0, num);
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                bis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新词发现
     *
     * @param input_file
     * @param output_file
     * @param min_freq    最小词频
     * @param min_mtro    最小聚合度
     * @param min_entro   最小自由度
     */
    public void findNewWord(String input_file, String output_file, String stopword_path, int min_freq, double min_mtro, double min_entro) {
        Map<String, Long> word_freq = count_words(input_file);   // 词频表
        int total_word = word_freq.size(); // 单词数量
        // 将单词切分为左单词和右单词，并通过单词聚合度筛选
        List<Map<String, List<Long>>> side_dict = lrg_info(word_freq, total_word, min_freq, min_mtro);
        // 计算单词的自由度
        List<Map<String, Double>> entro_list = cal_entro(side_dict);
        // 将子单词细分为左侧单词，右侧单词，可以在左侧或者右侧的单词
        List<Map<String, List<Double>>> entro_in_list = entro_lr_fusion(entro_list);
        // 通过自由度筛选，
        Map<String, Long> entro_dict = entro_filter(entro_in_list, word_freq, min_entro);
        // 根据词频进行排序
        Map<String, Long> result = sortByValue(entro_dict);
        // 去除停用词
        toStopWord(result, stopword_path);
        // 存储新词
        saveNewWord(output_file, result);
    }
}
