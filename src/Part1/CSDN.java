package Part1;

import java.net.InetAddress;

public class CSDN {
    public static void main(String[] args)
    throws Exception{
        String url = "www.zhihu.com";
        // 获取全部ip
        InetAddress[] addrs = InetAddress.getAllByName(url);
        System.out.println("总共ip数: "+addrs.length);
        for(int i=0; i<addrs.length; i++){
            System.out.println("ipAddress"+i+": "+addrs[i]);
        }
    }
}
