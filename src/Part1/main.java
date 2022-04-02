package Part1;

import java.net.InetAddress;

public class main {
    public static void main(String[] args)
    throws Exception{
        InetAddress addr = InetAddress.getLocalHost();
        // 获取本机ip地址
        System.out.println("Local HostAddress: "+addr.getHostAddress());
        // 获取本机名称
        System.out.println("Local host name: "+addr.getHostName());
    }
}
