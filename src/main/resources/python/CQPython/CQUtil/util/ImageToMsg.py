# coding=utf-8

from PIL import Image
import pytesseract
from io import BytesIO
import requests
from django.http import HttpResponse
import json

def imgToMsg(imgURL):
    response = requests.get(imgURL)
    response = response.content

    BytesIOObj = BytesIO()
    BytesIOObj.write(response)
    # Image.open(BytesIOObj).show()
    pytesseract.pytesseract.tesseract_cmd = 'C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe'
    text = pytesseract.image_to_string(Image.open(BytesIOObj),
                                       lang='chi_sim')  # Image.open()内改为识别的图片，lang=''内选择语言类型，将Tesseract OCR处理结果以字符串形式返回
    print('识图结果是:' + text.replace(' ', ''))
    return text.replace(' ', '')

def image_to_msg(request):
    json_str = request.body
    json_str = json_str.decode()
    req_data = json.loads(json_str)
    imgURL = req_data['image']
    result = []
    result.append(imgToMsg(imgURL))
    return HttpResponse(result)