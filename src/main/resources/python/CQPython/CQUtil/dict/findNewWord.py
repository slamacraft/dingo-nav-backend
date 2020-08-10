# -*- encoding:utf-8 -*-
import re
from math import log
from collections import Counter
from django.http import HttpResponse
import json

max_word_len = 6
re_chinese = re.compile(u"[\w]+", re.U)

def count_words(input_file):
    '''
    获取所有单词的出现次数
    :param input_file:
    :return:
    '''
    word_freq = Counter()
    fin = open(input_file, 'r', encoding='utf8')

    # 将文件每一行当成一句话，从每句话中读取单词
    for index, line in enumerate(fin):
        words = []
        # 找到所有的符合unicode的单词
        for sentence in re_chinese.findall(line):
            length = len(sentence)  # 句子的长度
            for i in range(length):     # 将句子逐字拆分，获得所有的词
                words += [sentence[i: j + i] for j in range(1, min(length - i + 1, max_word_len + 1))]
        # 更新单词的出现次数
        word_freq.update(words)
    fin.close()
    return word_freq


def toStopWord(entro_dict):
    '''
    去除停用词以及纯数字单词
    :param entro_dict: 单词词典
    :return:
    '''
    with open('D:\workspace\demo\src\main\python\CQPython\static\stopWords\CNstopwords.txt', 'r', encoding='utf-8') as f:
        line = f.read().strip()
        stopwords = line.split("\n")  # 以换行符分隔

    for stopword in stopwords:
        if stopword in entro_dict.keys():
            entro_dict.pop(stopword)

    for word in list(entro_dict.keys()):
        if word.isdigit():
            entro_dict.pop(word)


def lrg_info(word_freq, total_word, min_freq, min_mtro):
    '''
    获取所有聚合度与出现次数大于阈值的子单词
    :param word_freq: 单词的出现次数
    :param total_word: 累计单词数
    :param min_freq: 最小出现次数
    :param min_mtro: 最小聚合度
    :return:
    '''
    l_dict = {}
    r_dict = {}

    # 遍历单词表
    for word, freq in word_freq.items():
        if len(word) < 3:   # 如果词的长度小于等于二，则不用再切分
            continue

        # 将词进行切片，切去左边的字与右边的字形成子单词
        left_word = word[:-1]
        right_word = word[1:]

        def __update_dict(side_dict, side_word):
            side_word_freq = word_freq[side_word]   # 获取子单词的出现次数
            if side_word_freq > min_freq:           # 如果子单词的出现次数过少，忽略该单词
                # 计算子单词的聚合度
                # 聚合度 = p(父单词) / ( p(左子单词) * p(右子单词) )
                #        = ( freq(父单词) * 总词数 ) / ( freq(左子单词) * freq(右子单词) )
                mul_info1 = side_word_freq * total_word / (word_freq[side_word[1:]] * word_freq[side_word[0]])
                mul_info2 = side_word_freq * total_word / (word_freq[side_word[-1]] * word_freq[side_word[:-1]])

                # 获取聚合度最小的子单词组合
                # 如果该单词的聚合度最小的组合都能成词，那么该词一定会成词
                mul_info = min(mul_info1, mul_info2)
                if mul_info > min_mtro:     # 如果子单词的聚合度大于阈值，保存该单词及词频
                    if side_word in side_dict:
                        side_dict[side_word].append(freq)   # 如果该子单词存在，将父单词词频给子单词
                    else:
                        side_dict[side_word] = [side_word_freq, freq]   # 如果不存在，则新加上子单词与词频

        __update_dict(l_dict, left_word)
        __update_dict(r_dict, right_word)

    return l_dict, r_dict


def cal_entro(r_dict):
    '''
    计算子单词的自由度
    :param r_dict: 单词列表
    :return: 单词的自由度列表
    '''
    entro_r_dict = {}
    for word in r_dict:
        # <class 'list'>: [44, 9, 1, 1, 4, 1, 1, 3, 1, 9, 1, 1, 1, 1, 1, 1, 1, 2, 1]
        m_list = r_dict[word]

        # 获得这个词的出现次数数据，并去掉创建时的词频
        # <class 'list'>: [9, 1, 1, 4, 1, 1, 3, 1, 9, 1, 1, 1, 1, 1, 1, 1, 2, 1]
        r_list = m_list[1:]

        entro_r = 0
        sum_r_list = sum(r_list)    # 累计该词的出现次数
        for rm in r_list:
            # 计算该词的自由度
            # 自由度 = -p*log(p)
            entro_r -= rm / sum_r_list * log(rm / sum_r_list, 2)
        entro_r_dict[word] = entro_r

    return entro_r_dict


def entro_lr_fusion(entro_r_dict, entro_l_dict):
    '''
    将左右子单词拆分为：左子单词，右子单词，左右都有的子单词，用于通过自由度筛选
    :param entro_r_dict:
    :param entro_l_dict:
    :return:
    '''
    entro_in_rl_dict = {}
    entro_in_r_dict = {}
    entro_in_l_dict = entro_l_dict.copy()
    for word in entro_r_dict:
        if word in entro_l_dict:    # 如果有重复的词，将这个词单独保存
            entro_in_rl_dict[word] = [entro_l_dict[word], entro_r_dict[word]]
            entro_in_l_dict.pop(word)
        else:
            entro_in_r_dict[word] = entro_r_dict[word]  # 没有重复，保存该单词
    return entro_in_rl_dict, entro_in_l_dict, entro_in_r_dict


def entro_filter(entro_in_rl_dict, entro_in_l_dict, entro_in_r_dict, word_freq, min_entro):
    '''
    筛选自由度大于阈值的单词
    :param entro_in_rl_dict:左右都有词的单词表
    :param entro_in_l_dict:只有左边有词的单词表
    :param entro_in_r_dict:只有右边有词的单词表
    :param word_freq:单词总表
    :param min_entro:最小自由度
    :return:筛选完毕后的单词表
    '''
    entro_dict = {}
    for word in entro_in_rl_dict:
        if entro_in_rl_dict[word][0] > min_entro and entro_in_rl_dict[word][1] > min_entro:
            entro_dict[word] = word_freq[word]

    for word in entro_in_l_dict:
        if entro_in_l_dict[word] > min_entro:
            entro_dict[word] = word_freq[word]

    for word in entro_in_r_dict:
        if entro_in_r_dict[word] > min_entro:
            entro_dict[word] = word_freq[word]

    return entro_dict


def new_word_find(input_file, output_file, min_freq=10, min_mtro=80, min_entro=3):
    word_freq = count_words(input_file)
    total_word = sum(word_freq.values())

    l_dict, r_dict = lrg_info(word_freq, total_word, min_freq, min_mtro)

    entro_r_dict = cal_entro(l_dict)
    entro_l_dict = cal_entro(r_dict)

    entro_in_rl_dict, entro_in_l_dict, entro_in_r_dict = entro_lr_fusion(entro_r_dict, entro_l_dict)
    entro_dict = entro_filter(entro_in_rl_dict, entro_in_l_dict, entro_in_r_dict, word_freq, min_entro)
    toStopWord(entro_dict)

    result = sorted(entro_dict.items(), key=lambda x: x[1], reverse=True)

    with open(output_file, 'w', encoding='utf-8') as kf:
        for w, m in result:
            kf.write(w + '\t%d\n' % m)

def findNewWord(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    # input_file = '../../static/input/' + req_data['group_id'] + '.txt'
    # output_file = '../../static/input/' + req_data['group_id'] + '.txt'
    input_file = 'D:\workspace\demo\src\main\python\CQPython\static\input\\' + req_data['group_id'] + '.txt'
    output_file = 'D:\workspace\demo\src\main\python\CQPython\static\output\\' + req_data['group_id'] + '.txt'
    new_word_find(input_file, output_file, min_freq=5, min_mtro=50, min_entro=2.5)
    return HttpResponse('新词发现已经完成')

# 测试用的主类
if __name__ == '__main__':
    input_file = '../../static/input/' + '1021761429' + '.txt'
    output_file = '../../static/output/' + '1021761429' + '.txt'
    inputTest = 'D:\workspace\demo\src\main\python\CQPython\static\input\\1021761429.txt'
    new_word_find(inputTest, output_file, min_freq=5, min_mtro=50, min_entro=2.5)