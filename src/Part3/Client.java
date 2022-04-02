package Part3;

import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String args[]){
        System.out.println("与服务器连接成功");
        try{
            Socket server = new Socket("localhost", 1111);
            /*读取，传递服务器段数据的流*/
            DataInputStream input = new DataInputStream(server.getInputStream());

            String fname = input.readUTF();// 读取发送的文件名
            System.out.println("接收到的文件为： "+fname);
            String fileSave = "C:\\Users\\86135\\代码项目\\Idea项目\\SocketCoding\\dataTest.txt";// 文件保存为
            System.out.println("文件保存为: "+fileSave);
            File df = new File(fileSave);
            FileOutputStream fos = new FileOutputStream(df);// 本地文件流
            byte[] buffer = new byte[2048];
            int length = -1;

            /*构造结束标志*/
            byte[] endStr = "文件下载结束".getBytes(StandardCharsets.UTF_8);
            byte[] sendEnd = new byte[2048];
            for(int i=0; i<endStr.length; i++){
                sendEnd[i] = endStr[i];
            }

            while((length = input.read(buffer, 0, buffer.length)) != -1
                && !new String(buffer, StandardCharsets.UTF_8).equals(new String(sendEnd, StandardCharsets.UTF_8))){
                fos.write(buffer, 0, length);
            }
            fos.close();
            input.close();
            server.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}