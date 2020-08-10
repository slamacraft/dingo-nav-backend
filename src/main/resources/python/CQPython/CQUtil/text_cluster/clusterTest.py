#!/usr/bin/env python3
# -*-coding:utf-8-*-
import random
import jieba
import pandas as pd
import jiagu
import numpy as np
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import TfidfVectorizer
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans
import itertools as itertools
from sklearn.preprocessing import scale
import multiprocessing



def plot_cluster(result, newData, numClass):
    plt.figure(2)
    Lab = [[] for i in range(numClass)]
    index = 0
    for labi in result:
        Lab[labi].append(index)
        index += 1
    color = ['oy', 'ob', 'og', 'cs', 'ms', 'bs', 'ks', 'ys', 'yv', 'mv', 'bv', 'kv', 'gv', 'y^', 'm^', 'b^', 'k^',
             'g^'] * 3
    for i in range(numClass):
        x1 = []
        y1 = []
        for ind1 in newData[Lab[i]]:
            # print ind1
            try:
                y1.append(ind1[1])
                x1.append(ind1[0])
            except:
                pass
        plt.plot(x1, y1, color[i])
    # 绘制初始聚类的中心点
    x1 = []
    y1 = []
    for ind1 in clf.cluster_centers_:
        try:
            y1.append(ind1[1])
            x1.append(ind1[0])
        except:
            pass
    plt.plot(x1, y1, "rv")  # 绘制中心
    plt.show()


# 函数preprocess_text 用于分词
# 参数content_lines为上面转换的list
# 参数sentences是空list，用来储存分词后的数据
def preprocess_text(content_lines, sentences):
    for line in content_lines:
        try:
            words = line[0]
            segs = jieba.lcut(words)
            segs = [v for v in segs if not str(v).isdigit()]  # 去数字
            # segs = [v for v in segs if not str(v).isalnum()]  # 去数字与字母的组合
            segs = list(filter(lambda x: x.strip(), segs))  # 去左右空格
            segs = list(filter(lambda x: len(x) > 1, segs))  # 长度为1的字符
            segs = list(filter(lambda x: x not in stopwords, segs))  # 去掉停用词
            sentences.append(" ".join(segs))
        except Exception:
            print(line)
            continue



if __name__ == '__main__':
    stopwords = pd.read_csv('../../test/CNstopwords.txt', index_col=False, quoting=3, sep="\n", names=['stopword'], encoding='utf-8')
    stopwords = stopwords['stopword'].values

    # 加载文件
    laogong_df = pd.read_csv('../../test/input.txt', index_col=False, quoting=3, sep="\n", names=['input'], encoding='utf-8')
    laogong = laogong_df.values.tolist()

    sentences = []
    preprocess_text(laogong, sentences)

    resultdict = {}

    for i in range(0, len(laogong)):
        words = laogong[i][0]
        if words not in resultdict:
            resultdict[words] = {}
            resultdict[words]['cut'] = sentences[i]

    random.shuffle(sentences)  # 将得到的数据集打散
    print(sentences)

    # 将文本中的词语转换为词频矩阵 矩阵元素a[i][j] 表示j词在i类文本下的词频
    vectorizer = TfidfVectorizer(sublinear_tf=True, max_df=0.5)
    # 统计每个词语的tf-idf权值
    transformer = TfidfTransformer()
    # 第一个fit_transform是计算tf-idf 第二个fit_transform是将文本转为词频矩阵
    tfidf = transformer.fit_transform(vectorizer.fit_transform(sentences))
    # 获取词袋模型中的所有词语
    word = vectorizer.get_feature_names()
    # 将tf-idf矩阵抽取出来，元素w[i][j]表示j词在i类文本中的tf-idf权重
    weight = tfidf.toarray()
    # 查看特征大小
    print('Features length: ' + str(len(word)))

    numClass = 4  # 聚类分几簇
    clf = KMeans(n_clusters=numClass, max_iter=10000, init="k-means++",
                 tol=1e-6)  # 使用 k-means++ 来初始化模型，这里也可以选择随机初始化init="random"
    pca = PCA(n_components=100)  # 降维
    TnewData = pca.fit_transform(weight)  # 载入N维
    s = clf.fit(TnewData)

    pca = PCA(n_components=2)  # 输出两维
    newData = pca.fit_transform(weight)  # 载入N维
    result = list(clf.predict(TnewData))
    plot_cluster(result, newData, numClass)


