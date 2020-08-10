# -*- coding: utf-8 -*-

import re
from math import log
from collections import Counter

re_chinese = re.compile(u"[\w]+", re.U)
def count_words(input_file):
    '''
    获取所有的句子
    :param input_file:
    :return:
    '''
    fin = open(input_file, 'r', encoding='utf8')

    # 将文件每一行当成一句话，从每句话中读取单词
    msg = []
    for index, line in enumerate(fin):
        # 找到所有的符合unicode的单词
        if re.search(u"[【】]", line):
            attribute = line.split('】')
            attr = {}
            attr['msg'] = re.sub(u'\[[^\]]+\]', '', attribute[2])
            if attr['msg'].isspace():    # 空字符串就不要了
                continue
            attr['msg'].strip()
            attr['time'] = attribute[0][1:]
            attr['user'] = attribute[1][1:]
            msg.append(attr)
        else:
            msg[len(msg)-1]['msg'] += line

    fin.close()
    return msg


def preprocess(msg):
    '''
    初始化对话语料
    :param msg:
    :return:
    '''

    msgStr = ""
    msgUser = ""
    for i in range(len(msg)-1, -1, -1):
        if msgUser == msg[i]['user']:     # 将同一个用户的多个语句合并为一句
            msg[i]['msg'] += msgStr
            msg.pop(i)
        else:
            if msgStr != msg[i]['msg']:  # 不同用户发送不同的句子，那么就是正常对话
                msgUser = msg[i]['user']
                msgStr = ""
            else:                       # 不同用户发送相同的句子，那么就是复读，删掉复读语句
                msgUser = msg[i]['user']
                msg.pop(i)


def saveUpdateMsg(output_file, msg):
    with open(output_file, 'w', encoding='utf-8') as kf:
        kf.write("状态" + '\t' + "问题" + '\t' + "答案" + '\n')
        for i in range(len(msg)-1):
            if msg[i]['msg'].replace('\n', '').isalnum() or len(msg[i]['msg']) < 10:
                continue
            kf.write("启用" + '\t' + msg[i]['msg'].replace('\n', '').replace(' ', '') + '\t' + msg[i+1]['msg'].replace('\n', '').replace(' ', '') + '\n')
    # with open(output_file, 'w', encoding='utf-8') as kf:
    #     for i in range(len(msg)-1):
    #         kf.write(msg[i]['msg'].replace('\n', '') + '\n')


# if __name__ == "__main__":
#     msg = count_words('../../test/GroupMsg.txt')
#     preprocess(msg)
#     saveUpdateMsg('../../test/toUpdateMsg.txt', msg)