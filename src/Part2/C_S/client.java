package Part2.C_S;
import java.net.Socket;
import java.io.*;

public class client {
    public static void main(String args[]){
        try{
            Socket client = new Socket("localhost",1111);//创建客户端套接字
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));//获取输入流 获得服务器返回的数据
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader localMessage = new BufferedReader(new InputStreamReader(System.in));//接受客户端从键盘输入的信息
            System.out.println("从服务器"+client.getRemoteSocketAddress()+"返回的消息："+reader.readLine());

            while(true){//从客户端向服务器传输数据
                String message,str;
                message = localMessage.readLine();//从屏幕读取一行数据
                out.println(message);// 传输给服务器
                str = reader.readLine();
                System.out.println("从服务器"+client.getRemoteSocketAddress()+"返回的消息："+str);
                if(str.equals("Bye") || str.equals("bye")){//服务器传回 结束 数据
                    System.out.println("连接断开！");
                    break;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}