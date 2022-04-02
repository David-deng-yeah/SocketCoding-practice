package Part3;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;

/*服务器类*/
public class Sever{
    public static void main(String args[]){
        System.out.println("服务器运行开始OKOKOKOKO！！");
        /*创建服务器*/
        try {
            ServerSocket server = new ServerSocket(1111);
            int threadID = 1;
            while(true){
                /*建立连接，每获取一个客户端就开启一个线程*/
                Socket client = server.accept();
                System.out.println("服务器的线程"+threadID+"启动，与客户端"+threadID+"连接成功");
                ++threadID;
                new Thread(new FileThread(client)).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

/*文件下载服务线程类*/
class FileThread implements Runnable{
    private Socket client;
    public FileThread(Socket client){
        this.client = client;
    }
    /*线程*/
    @Override
    public void run(){
        try {
            /*客户端的输入输出流*/
            DataInputStream input = new DataInputStream(client.getInputStream());// 客户端输入流
            DataOutputStream output = new DataOutputStream(client.getOutputStream());// 客户端输出流

            /*获取文件的输入流*/
            FileInputStream fs = new FileInputStream("C:\\Users\\86135\\代码项目\\Idea项目\\SocketCoding\\src\\Part2.C_S\\data.txt");
            output.writeUTF("C:\\Users\\86135\\代码项目\\Idea项目\\SocketCoding\\src\\Part3\\data.txt");// 发送要下载的文件名


            /*构建结束标志*/
            byte[] endStr = "文件下载结束".getBytes(StandardCharsets.UTF_8);
            byte[] sendEnd = new byte[2048];
            for(int i=0; i<endStr.length; i++){
                sendEnd[i] = endStr[i];
            }

            byte[] buffer = new byte[2048];
            int length = -1;
            System.out.println("开始传输文件");
            while((length = fs.read(buffer)) != -1){
                output.write(buffer);// 向客户端写入
            }
            System.out.println("文件传输结束");

            /*释放资源*/
            fs.close();

            output.write(sendEnd);

            input.close();
            output.close();
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
