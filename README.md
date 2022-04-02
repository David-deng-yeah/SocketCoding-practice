# SocketCoding-practice

# 实验内容
* InetAddress类和URLConnection类的使用
* Socket、ServerSocket类和DatagramPacket 、DatagramSocket类的使用
* 通过Socket和ServerSocket类实现TCP文件传输

# 实验步骤：

## 第一部分
* 使用InetAddress类的方法获取本地机的名称和IP地址

* 使用InetAddress类的方法获取网站www.csdn.net的IP地址，如果存在多个IP地址，要求全部返回

* 使用URL类下载深圳大学首页http://www.szu.edu.cn，并统计下载得到网页文件的大小

## 第二部分
> 利用Socket类和ServerSocket类编写一个C/S程序，实现C/S通信

客户端向服务器端发送Time命令，服务器端接受到该字符串后将服务器端当前时间返回给客户端；客户端向服务器端发送Exit命令，服务器端向客户端返回“Bye”后退出

编写完整程序；一个服务器端程序，一个客户端程序。服务器端和客户端都需要打印出接受到的消息和发出的命令
下图为运行结果示例

 ![image](https://user-images.githubusercontent.com/65102150/161391780-1fcfbe06-0354-4d92-b498-f3eac1611d45.png)


> 编写一数据报通信程序，实现简单的聊天功能。

“聊天内容”和“输入文本”分别为当前聊天的历史信息和当前要传送出去的聊天文本。“确定”、“清空”、“退出”三个按钮分别实现发送当前聊天文本、清空当前聊天文本和退出系统的功能。
界面可参考如下格式
 
![image](https://user-images.githubusercontent.com/65102150/161391775-0ad83db9-ab4c-4759-aa57-860939dded45.png)

##  第三部分
> 利用Socket类和ServerSocket类，编写一个C/S程序，实现网络文件传输。
> 客户端向服务器端发送请求，服务器端当接受到客户端的请求之后，先向其传输文件名，当客户端接受完毕之后，向客户端传输文件。
> 客户端连上服务器后接收传输文件，并进行改名（文件名可自行定义）存在本地。
> 编写完整程序；一个服务器端程序，一个客户端程序。服务器端和客户端都需要打印出交互过程。
> 下图为运行结果示例

* 服务端运行结果

![image](https://user-images.githubusercontent.com/65102150/161391950-c40e8052-bfca-43c5-97c7-adc9ce41df51.png)

* 客户端运行结果

![image](https://user-images.githubusercontent.com/65102150/161391956-eeb15a6f-ba54-4481-a539-ea799e3511db.png)




