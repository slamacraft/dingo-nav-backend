from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
import sys
import jieba
import jieba.posseg as pseg
import json
import jiagu
import jieba.analyse as anal

"""
 django.http模块中定义了HttpResponse 对象的API
 作用：不需要调用模板直接返回数据
 HttpResponse属性：
    content: 返回内容,字符串类型
    charset: 响应的编码字符集
    status_code: HTTP响应的状态码
"""

"""
hello 为一个视图函数，每个视图函数必须第一个参数为request。哪怕用不到request。
request是django.http.HttpRequest的一个实例
"""

def jiaguFindword():
    jiagu.findword('../test/input.txt', '../test/output.txt')  # 根据文本，利用信息熵做新词发现。

def jiebaCut(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    text = req_data['msg']
    #jieba.load_userdict("static/dict/dict.txt")
    words = pseg.cut(text)
    result = ""
    for w in words:
        result += w.word + " " + w.flag + "\n"
    #print(result)
    return HttpResponse(result)


def jiebaCutTest(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    text = req_data['msg']
    return HttpResponse(text)

def jiebaAddWord(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    word = req_data['word']
    posseg = req_data['posseg']
    if posseg is None or posseg is '':  #没有指定词性，默认为名词
        jieba.add_word(word, freq=10, tag='n')
    else:                               #指定词性
        jieba.add_word(word, freq=10, tag=posseg)
    return HttpResponse("ok")

def jiebaExtractTags(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    text = req_data['msg']
    result = []
    for word in anal.extract_tags(text):
        result.append(word)
        result.append("\n")
    return HttpResponse(result)

if __name__ == '__main__':
    jiaguFindword()
