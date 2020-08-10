# -*- coding: utf-8 -*-
import requests

userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36"
csrfmiddlewaretoken = "KUPtnuOTfgUAlRwKsBE1Na4ddJ7cuULEMabJtkySuiEON3JKeSlQRxBqw3Sa0hJJ"
cookie = "csrftoken=KUPtnuOTfgUAlRwKsBE1Na4ddJ7cuULEMabJtkySuiEON3JKeSlQRxBqw3Sa0hJJ"
header = {
    # "origin": "https://passport.mafengwo.cn",
    "Referer": "https://console.ownthink.com/",
    'User-Agent': userAgent,
    # 'csrfmiddlewaretoken': csrfmiddlewaretoken,
    # 'cookie' : cookie,
}

def mafengwoLogin(account, password):
    # 马蜂窝模仿 登录
    print ("开始模拟登录思知机器人")

    postUrl = "https://console.ownthink.com/"
    postData = {
        "passport": account,
        "password": password,
    }
    responseRes = requests.post(postUrl, data = postData, headers = header)
    # 无论是否登录成功，状态码一般都是 statusCode = 200
    print(f"statusCode = {responseRes.status_code}")
    print(f"text = {responseRes.text}")

# if __name__ == "__main__":
#     # 从返回结果来看，有登录成功
#     mafengwoLogin("1114951452@qq.com", "13574159100Q")