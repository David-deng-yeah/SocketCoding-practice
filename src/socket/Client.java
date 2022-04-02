package socket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static String IP_ADDR = "localhost";// 服务器地址
    public static final int PORT = 12345;// 服务器端口号

    public static void main(String[] args) {
        System.out.print("请输入要连接的服务器IP地址：");
        Scanner in = new Scanner(System.in);
        IP_ADDR = in.next(); // 获取服务器地址

        System.out.println("\n------------------客户端启动--------------------\n");

        boolean f1 = true; // 是否退出的标志变量
        while (f1) {
            System.out.print("菜单：\n1、注册\n2、登录\n3、退出\n请输入你的选择序号：");
            int select = in.nextInt();
            switch (select) {
                case 1: { // 注册
                    Socket socket = null;
                    try {
                        System.out.println("------------------开始注册--------------------");
                        System.out.println("请输入用户名(不得包含\"#\"):");
                        String username = in.next();
                        if (username.contains("#")) { // 检查用户名是否合法
                            System.out.println("用户名不合法");
                            continue; // 跳出本次循环继续
                        }
                        System.out.println("请输入密码(不得包含\"#\"):");
                        String password = in.next();
                        if (password.contains("#")) { // 检查密码是否合法
                            System.out.println("密码不合法");
                            continue; // 跳出本次循环继续
                        }

                        // 创建一个流套接字并将其连接到指定主机上的指定端口号
                        socket = new Socket(IP_ADDR, PORT);
                        // 向服务器传递数据的流
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        // 读取服务器端数据的流
                        DataInputStream input = new DataInputStream(socket.getInputStream());

                        out.writeUTF("申请注册");

                        String usernamepassword = username + "#" + password;
                        out.writeUTF(usernamepassword); // 传送注册信息

                        String msg = input.readUTF();

                        System.out.println("服务器端：" + msg + "\n");

                    } catch (IOException e) {
                        System.out.println("客户端注册异常：" + e.getMessage());
                    } finally {
                        // 关闭socket连接，释放资源
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                socket = null;
                                e.printStackTrace();
                                System.out.println("客户端 finally 异常:" + e.getMessage());
                            }
                        }
                    }
                    break;
                }

                case 2: { // 登录
                    Socket socket = null;
                    try {
                        System.out.println("\n------------------开始登录--------------------");
                        System.out.println("请输入用户名：");
                        String username = in.next();
                        System.out.println("请输入密码：");
                        String password = in.next();

                        String usernamepassword = username + "#" + password;

                        // 创建一个流套接字并将其连接到指定主机上的指定端口号
                        socket = new Socket(IP_ADDR, PORT);
                        // 向服务器传递数据的流
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        // 读取服务器端数据的流
                        DataInputStream input = new DataInputStream(socket.getInputStream());

                        out.writeUTF("申请登录"); // 向服务器提交登录的请求，等待服务器响应
                        out.writeUTF(usernamepassword); // 向服务器上传登录信息以验证

                        boolean msg = input.readBoolean(); // 读取服务器响应的信号，判断是否可以登录

                        if (msg) { // 登陆成功
                            System.out.println("登录验证成功！\n");
                            // 开始进入上传下载文件模块
                            boolean f = true;
                            while (f) {
                                System.out.print("菜单：\n1、上传文件\n2、下载文件\n3、退出\n请输入你的选择序号：");
                                int choice = in.nextInt();
                                switch (choice) {
                                    case 1: { // 上传文件
                                        try {

                                            out.writeUTF("申请上传文件"); // 向服务器提出上传文件的申请
                                            // 向服务器端上传文件
                                            System.out.println("\n请输入要上传的文件的路径（绝对路径或者相对于应用程序的相对路径）:");
                                            String uploadFileName = in.next();
                                            // 先把文件名传给服务器
                                            out.writeUTF("/" + uploadFileName);
                                            System.out.println("等待服务器确认接收文件...");
                                            boolean ret = input.readBoolean(); // 以utf编码读取
                                            if (!ret) {
                                                System.out.println("服务器端拒绝接收此文件!");
                                                continue;
                                            } else {
                                                BufferedInputStream fin = new BufferedInputStream(
                                                        new FileInputStream(new File(uploadFileName)));
                                                byte[] buffer = new byte[1024];
                                                int flag = -1;
                                                System.out.print("上传文件中...");
                                                while ((flag = fin.read(buffer)) != -1) {
                                                    // 当未读取结束的时候就一直读取并且发送
                                                    out.write(buffer);
                                                }
                                                fin.close();
                                                System.out.println("\n上传结束!\n");
                                            }
                                            // out.close(); //不能这样写因为会把socket也关闭
                                            // socket.shutdownOutput(); //关闭输出流而不关闭socket ,最后发现也不能这样，因为output无法再次打开
                                            // 构造结束标志
                                            byte[] endstr = "文件上传结束".getBytes("utf-8");
                                            byte[] sendend = new byte[1024];
                                            for (int i = 0; i < endstr.length; i++) {
                                                sendend[i] = endstr[i];
                                            }
                                            out.write(sendend);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("客户端异常:" + e.getMessage());
                                        } finally {
                                            continue;
                                        }
                                    }

                                    case 2: { // 下载文件
                                        out.writeUTF("申请下载文件"); // 向服务器提出下载文件的申请
                                        boolean candown = input.readBoolean();
                                        if (candown) {
                                            System.out.println(input.readUTF());
                                            int fileId = in.nextInt();
                                            if (fileId == 0) {
                                                out.writeInt(0);
                                                continue;
                                            } else {
                                                out.writeInt(fileId);
                                                // 获取文件名
                                                String fname = input.readUTF();
                                                // 接收文件
                                                File df = new File("download/" + fname);
                                                if (!df.getParentFile().exists()) {
                                                    df.getParentFile().mkdir();
                                                }
                                                if (!df.exists()) {
                                                    df.createNewFile();
                                                }
                                                FileOutputStream fos = new FileOutputStream(df);
                                                byte[] buffer = new byte[1024];
                                                int flag = -1;
                                                System.out.print("下载文件中...");

                                                // 构造结束标志
                                                byte[] endstr = "文件下载结束".getBytes("utf-8");
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
                                                System.out.println("文件下载结束！\n");
                                                continue;
                                            }
                                        } else {
                                            System.out.println("服务器上暂无文件可以下载！");
                                            continue;
                                        }
                                    }

                                    case 3: { // 退出上传下载
                                        out.close();
                                        input.close();
                                        f = false;
                                        System.out.println("客户端已退出文件上传下载模块。");
                                        break;
                                    }

                                    default: {
                                        out.close();
                                        input.close();
                                        break;
                                    }

                                }
                            }

                        } else { // 登录失败
                            System.out.println("登录验证失败！\n");
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    socket = null;
                                    //e.printStackTrace();
                                    System.out.println("客户端 finally 异常:" + e.getMessage());
                                }
                            }
                            continue;
                        }

                    } catch (IOException e) {
                        System.out.println("客户端登录异常：" + e.getMessage());
                    } finally {
                        break;
                    }

                }

                case 3: {
                    System.out.println("--------------------关闭客户端--------------------");
                    f1 = false;
                    break;
                }

                default: {
                    break;
                }

            }
        }

    }
}