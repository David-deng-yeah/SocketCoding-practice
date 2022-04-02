package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//用户类
class User {
    public String username;
    public String password;
    public List<String> uploadFiles; // 存储上传过的文件名称
}

/**
 * 服务器端程序
 *
 * @author NP_hard
 *
 */
public class Server {
    public static final int PORT = 12345;// 监听的端口号
    // 账户列表
    public static List<User> userList = new ArrayList<User>();

    public static void main(String[] args) {
        System.out.println("-------------------服务器启动------------------\n");
        Server server = new Server();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    // 处理数据传输的线程
    private class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
            System.out.println("调用一次处理线程");
        }

        public void run() {
            try {
                // 读取客户端数据的流
                DataInputStream input = new DataInputStream(socket.getInputStream());
                // 向客户端回复信息的流
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // 接收客户端的选项
                String clientInputStr = input.readUTF();

                switch (clientInputStr) {
                    case "申请注册": {
                        String usernamepassword = input.readUTF(); // 获取注册的信息
                        String[] strs = usernamepassword.split("#");
                        // 根据上传的注册信息添加用户
                        User user = new User();
                        user.username = strs[0];
                        user.password = strs[1];
                        user.uploadFiles = new ArrayList<String>();
                        userList.add(user);
                        System.out.println("成功添加用户：" + user.username);
                        // System.out.println(userList);
                        out.writeUTF("注册成功！");
                        break;
                    } // #case "申请注册"

                    case "申请登录": {
                        String usernamepassword = input.readUTF(); // 获取注册的信息
                        String[] strs = usernamepassword.split("#");

                        System.out.println("客户端试图登录的帐号：" + strs[0]);

                        boolean canlogin = false;
                        User curuser = null;
                        for (User u : userList) {
                            if (u.username.equals(strs[0]) && u.password.equals(strs[1])) { // 可以登录
                                curuser = u;
                                canlogin = true;
                                break;
                            }
                        }

                        if (canlogin) { // 用户名和密码正确
                            out.writeBoolean(true); // 向客户端返回可以登录的信号
                            System.out.println("验证成功，已登录！\n");

                            boolean rei = true; // 循环接收传送文件
                            while (rei) {

                                // 接收到模块编号
                                clientInputStr = input.readUTF();
                                switch (clientInputStr) {
                                    case "退出文件上传下载模块": { // 退出文件上传下载模块
                                        rei = false;
                                        break;
                                    }
                                    case "申请上传文件": { // 申请上传文件
                                        clientInputStr = input.readUTF(); // 接收文件名
                                        System.out.println("客户端上传的文件的文件名为:" + clientInputStr.substring(1) + "  是否接收此文件?（y/n）");
                                        char receive = new Scanner(System.in).nextLine().toCharArray()[0];
                                        receive = Character.toLowerCase(receive);

                                        if (receive == 'y') {
                                            out.writeBoolean(true);
                                            File f = new File("upload/" + clientInputStr
                                                    .substring(clientInputStr.lastIndexOf("/") + 1, clientInputStr.length()));
                                            if (!f.getParentFile().exists()) {
                                                f.getParentFile().mkdir();
                                            }
                                            if (!f.exists()) {
                                                f.createNewFile();
                                            }
                                            FileOutputStream fos = new FileOutputStream(f);
                                            byte[] buffer = new byte[1024];
                                            int flag = -1;
                                            System.out.print("接收文件中...");

                                            // 构造结束标志
                                            byte[] endstr = "文件上传结束".getBytes("utf-8");
                                            byte[] sendend = new byte[1024];
                                            for (int i = 0; i < endstr.length; i++) {
                                                sendend[i] = endstr[i];
                                            }

                                            // 当未读取到结束标志的时候就一直读取
                                            while ((flag = input.read(buffer)) != -1
                                                    && !new String(buffer, "utf-8").equals(new String(sendend, "utf-8"))) {
                                                fos.write(buffer);
                                                buffer = new byte[1024];
                                            }
                                            fos.close();
                                            System.out.println("\n文件上传结束!\n");
                                        } else {
                                            System.out.println("已经拒绝客户端的服务器上传!");
                                            out.writeBoolean(false);
                                        }

                                        continue;

                                    }
                                    case "申请下载文件": { // 申请下载文件
                                        // 首先获取所有upload目录下的文件
                                        File uploadDir = new File("upload");
                                        if (uploadDir.exists()) { // 存在该目录
                                            out.writeBoolean(true);
                                            // 获取目录下所有文件
                                            File[] files = uploadDir.listFiles();
                                            String hint = "服务器上一共有" + files.length + "个文件\n0、退出下载\n";
                                            for (int i = 0; i < files.length; i++) {
                                                hint += (i+1)+"、"+files[i].getName()+"\n";
                                            }
                                            hint += "请输入需要下载的文件序号:";
                                            out.writeUTF(hint);
                                            int fileId=input.readInt();
                                            if(fileId==0) {
                                                continue;
                                            }
                                            out.writeUTF(files[fileId-1].getName());
                                            // 构造结束标志
                                            byte[] endstr = "文件下载结束".getBytes("utf-8");
                                            byte[] sendend = new byte[1024];
                                            for (int i = 0; i < endstr.length; i++) {
                                                sendend[i] = endstr[i];
                                            }

                                            //获取文件输入流
                                            FileInputStream fin = new FileInputStream(files[fileId-1]);
                                            byte[] buffer = new byte[1024];
                                            int readflag=-1;
                                            System.out.println("正在向客户端发送文件...");
                                            while((readflag=fin.read(buffer))!=-1) {
                                                out.write(buffer);
                                            }
                                            fin.close();
                                            out.write(sendend);
                                            System.out.println("文件发送成功！\n");
                                            continue;

                                        } else { // 目录不存在，说明无法下载
                                            out.writeBoolean(false);
                                        }
                                        continue;
                                    }
                                    default: {
                                        break;
                                    }

                                }

                            }

                        } else {
                            out.writeBoolean(false); // 向客户端返回不能登录的信号
                            System.out.println("验证失败不允许登录！\n");
                        }
                    } // #case "申请登录"

                }// #switch
                // 关闭流
                out.close();
                input.close();
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        //e.printStackTrace();
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }

            }
        }
    }
}