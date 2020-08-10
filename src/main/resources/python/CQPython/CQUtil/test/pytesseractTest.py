# coding=utf-8

from PIL import Image
import pytesseract
from io import BytesIO
import requests

def test():
    response = requests.get('https://gchat.qpic.cn/gchatpic_new/1114951452/586747606-2801688756-DD93538A5A4C26238769C7EF719AF930/0?vuin=2270374713&amp;term=2')
    response = response.content

    BytesIOObj = BytesIO()
    BytesIOObj.write(response)
    Image.open(BytesIOObj).show()
    pytesseract.pytesseract.tesseract_cmd = 'C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe'
    text = pytesseract.image_to_string(Image.open(BytesIOObj),
                                       lang='chi_sim')  # Image.open()内改为识别的图片，lang=''内选择语言类型，将Tesseract OCR处理结果以字符串形式返回
    print('识图结果是:' + text)

# if __name__ == "__main__":
#     # 从返回结果来看，有登录成功
#     test()