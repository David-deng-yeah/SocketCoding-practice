package Part2.C_S;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;//时间


//import java.util.Calendar;
public class server {
    public static void main(String args[]){
        try{
            ServerSocket server = new ServerSocket(1111);//创建服务器套接字
            System.out.println("服务器启动完毕");
            System.out.println("等待客户端连接...");
            Socket socket = server.accept();//等待客户端连接
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//获得客户端的输入
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);//获得客户端输出流
            if(socket.isConnected()){
                System.out.println("创建连接，客户端名称为"+socket.getInetAddress().getHostAddress()+"  连接成功！");
                out.println("恭喜你，连接成功");
            }
            while(true){
                String str = reader.readLine();
                if(str.equals("Time") || str.equals("time")){//客户端请求时间数据
                    Date date = new Date();
                    System.out.println("客户端请求当前时间");
                    DateFormat format = DateFormat.getDateInstance();
                    out.println(format.format(date));//格式化输出时间
                }
                else if(str.equals("Exit") || str.equals("exit")){//退出
                    out.println("bye");
                    System.out.println("连接断开");
                    break;
                }
                else{
                    System.out.println("数据："+str);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}