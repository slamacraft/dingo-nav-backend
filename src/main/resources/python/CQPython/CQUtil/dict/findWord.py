# -*- coding: utf-8 -*-
import re
import math
import pandas as pd
import string
from collections import OrderedDict

class NewWord(object):
    def __init__(self, max_len_word, radio):
        self.max_len_word = max_len_word
        self.radio = radio
        self.words = {}

    def find_words(self, doc):
        '''
        找出所有可能出现的词
        :param doc:
        :param max_len_word:
        :return:
        '''
        len_doc = len(doc)
        for i in range(len_doc):
            for j in range(i + 1, i + self.max_len_word + 1):
                if doc[i:j] in self.words:
                    self.words[doc[i:j]]['freq'] += 1
                else:
                    self.words[doc[i:j]] = {}
                    self.words[doc[i:j]]['freq'] = 1

    def toStopWord(self):
        '''
        去除停用词
        :return:
        '''

        with open('../../test/CNstopwords.txt', 'r', encoding='utf-8') as f:
            line = f.read().strip()
            stopwords = line.split("\n")  # 以换行符分隔

        for stopword in stopwords:
            if stopword in self.words.keys():
                self.words.pop(stopword)


    def dop(self):
        '''
        计算聚合度
        :param words:
        :return:
        '''
        len_words = len(self.words)
        # 计算每一个词频
        # 词频 = 词的出现次数 / 词的最大字数 * 词的总数
        for k, v in self.words.items():
            self.words[k]['freq_radio'] = self.words[k]['freq']/(self.max_len_word * len_words)

        # 计算每一个词的聚合度
        for k, v in self.words.items():
            dop = []
            length = len(k)          # 词的字数
            if length == 1:          # 如果词的长度为1，则其聚合度为0
                self.words[k]['dop'] = 0
            else:
                for i in range(1, length):
                    word = self.words[k[0:i]]['freq_radio'] * self.words[k[i:length]]['freq_radio']
                    dop.append(word)
                dop = min(dop)
                self.words[k]['dop'] = math.log(self.words[k]['freq_radio']/dop)

    def left_free(self, doc):
        '''
        计算左自由度
        :param words:
        :return:
        '''
        for k, v in self.words.items():
            left_list = [m.start() for m in re.finditer(k, doc) if m.start() != 1]
            len_left_list = len(left_list)
            left_item = {}
            for li in left_list:
                if doc[li-1] in left_item:
                    left_item[doc[li-1]] += 1
                else:
                    left_item[doc[li-1]] = 1
            left = 0
            for _k, _v in left_item.items():
                left += abs((left_item[_k]/len_left_list) * math.log(1/len(left_item)))
            self.words[k]['left_free'] = left

    def right_free(self, doc):
        '''
        计算右自由度
        :param words:
        :return:
        '''
        for k, v in self.words.items():
            right_list = [m.start() for m in re.finditer(k, doc) if m.start() < len(doc)-5]
            len_right_list = len(right_list)
            right_item = {}
            for li in right_list:
                if doc[li+len(k)] in right_item:
                    right_item[doc[li+len(k)]] += 1
                else:
                    right_item[doc[li+len(k)]] = 1
            right = 0
            for _k, _v in right_item.items():
                right += abs((right_item[_k]/len_right_list) * math.log(1/len(right_item)))
            self.words[k]['right_free'] = right

    def get_df(self):
        df = pd.DataFrame(self.words)
        df = df.T
        df['score'] = df['dop'] + df['left_free'] + df['right_free']
        # 分数 = - log(词频) * (凝聚度) * (自由度)
        # df['score'] = df['freq_radio'] * df['dop'] * (df['left_free'] + df['right_free'])
        df = df.sort_values(by='score', ascending=False)
        df = df[df['score'] > self.radio]
        df = df[df['freq'] > 5]
        df = df[df['left_free'] > 1]
        df = df[df['right_free'] > 1]
        return df

    def run(self, doc):

        p = re.compile(u"[^\u4e00-\u9fa5a-zA-Z0-9]")
        doc = p.sub('', doc)
        # doc = re.sub('[🐴🐎 ,，.。"“”‘’\';；:：、？?！!\n\[\]\(\)【】（）\\/a-zA-Z0-9]', '', doc)
        self.find_words(doc)
        self.dop()
        self.left_free(doc)
        self.right_free(doc)
        self.toStopWord()
        df = self.get_df()
        return df

# if __name__ == '__main__':
#     doc = open('../../test/input.txt', 'r', encoding='utf-8').read()
#     nw = NewWord(max_len_word=3, radio=12.5)
#     df = nw.run(doc)
#     df.to_csv('../../test/output.txt', sep='|', encoding='utf-8')