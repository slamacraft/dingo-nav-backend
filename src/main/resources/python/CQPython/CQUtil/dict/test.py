# -*- coding: utf-8 -*-

import sys
import jieba
import jieba.posseg as pseg
import jiagu

#reload(sys)
#sys.setdefaultencoding("utf-8")

#定义个函数式用于分词
def jiebaclearText(text):
    docs = [
        "百度深度学习中文情感分析工具Senta试用及在线测试",
        "情感分析是自然语言处理里面一个热门话题",
        "AI Challenger 2018 文本挖掘类竞赛相关解决方案及代码汇总",
        "深度学习实践：从零开始做电影评论文本情感分析",
        "BERT相关论文、文章和代码资源汇总",
        "将不同长度的句子用BERT预训练模型编码，映射到一个固定长度的向量上",
        "自然语言处理工具包spaCy介绍",
        "现在可以快速测试一下spaCy的相关功能，我们以英文数据为例，spaCy目前主要支持英文和德文"
    ]
    with open('../../test/input.txt', 'r', encoding='utf-8') as f:
        line = f.read().strip()
        words = line.split("\n")  # 以换行符分隔
    cluster = jiagu.text_cluster(words)
    file = open("../../test/test.txt", mode="wb")
    file.write(cluster)
    file.flush()
    file.close()
    print(cluster)

# if __name__=="__main__":
#     jiebaclearText(1)