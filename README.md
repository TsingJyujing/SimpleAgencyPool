# 简单代理池
## 简介
中国特色程序，阿里云不知道用什么手段检测了代理，一般的HTTP代理都走不通。
即使我做一个API：
http://my.site.net/api/agency?url=http://www.google.com/
返回内容是网页，也会被封禁，我后来发现，其对出入的内容进行了过滤。
这个时候可以选择ShadowSocket。
考虑到我只需要在爬虫里使用，这里有一个更加简单的方案，就是仅仅对数据进行了Base64编码，结果组了个JSON。
关于编码之后数据变大的问题，我在SpringBoot中启用了Zip压缩，其实网络的流量不会很大（其实只是虚胖而已，哼）


## 编译和运行

首先是编译，用Maven打包就好了，然后把打包的lib文件夹和生成的JAR复制到你的服务器上

随后启动：

java -jar spider_agency-1.0-SNAPSHOT.jar --server.port=8087

如果你是远程SSH登陆Linux的话：

nohup java -jar spider_agency-1.0-SNAPSHOT.jar --server.port=8087 >/dev/null &

然后就可以了

## 接口使用

平时写爬虫都用Python，给出一份爬虫使用的代码：
使用的时候直接urlread(你的URL)即可

```python
#!/bin/python
# -*- coding: utf-8 -*-
"""
Created on 2017-2-3
@author: Yuan Yi fan
"""
import os
import string
import urllib
import base64
import json
import urllib2
import re

# 请求超时设置
request_timeout = 25

# 浏览器的User Agent参数
user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + \
             "AppleWebKit/537.36 (KHTML, like Gecko) " + \
             "Chrome/42.0.2311.135 " + \
             "Safari/537.36 " + \
             "Edge/12.10240"

mobile_user_agent = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_2 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/47.0.2526.70 Mobile/13C71 Safari/601.1.46"
proxy_keywords = re.compile("(%s)" % "|".join(["google", "xhamster"]))
# XML解析器
XML_decoder = 'lxml'


def get_request_head(url):
    req_header = {
        'User-Agent': mobile_user_agent,
        'Accept': '"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"',
        'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3',
        'Host': re.findall('://.*?/', url, re.DOTALL)[0][3:-1]
    }
    return req_header


def get_request(url):
    return urllib2.Request(url, None, get_request_head(url))


def urlread(url, retry_times=10):
    for i in range(retry_times):
        try:
            if len(proxy_keywords.findall(url))<=0:
                print("Proxy unused...")
                return urllib2.urlopen(get_request(url), None, request_timeout).read()
            else:
                print("Proxy used...")
                data = json.loads(urllib2.urlopen("http://你的服务器IP:你刚才开的端口/agency/get?url=%s" % base64.b64encode(url)).read())
                if data["status"]=="success":
                    return base64.b64decode(data["data"])
                else:
                    print("Status is not success, retrying...")
        except:
            pass
    print("Error while reading:%s" % url)
```
