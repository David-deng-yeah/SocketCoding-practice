package Part1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class SZU {
    public static void main(String[] args)
    throws Exception{
        URL url = new URL("https://www.szu.edu.cn/");
        InputStream in = url.openStream();
        FileOutputStream fout = new FileOutputStream(new File("szu.html"));
        int a = 0;
        while(a > -1){
            a = in.read();
            fout.write(a);
        }
    }
}
