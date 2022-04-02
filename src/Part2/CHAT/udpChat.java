package Part2.CHAT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
        
public class udpChat extends JFrame {

    private static final int DEFAULT_PORT = 8888;

    //把主窗口分为 NORTH.CENTER.SOUTH 三部分
    private JLabel stateLB;                 //显示监听状态
    private JTextArea centerTextArea;       //显示聊天记录
    private JPanel sourthPanel;             //最下面的面板
    private JTextArea inputTextArea; 		//聊天输入框
    private JPanel bottomPanel;				//放置IP输入框，按钮等
    private JTextField ipTextField;   		//IP输入框
    private JTextField remotePortTF;  		//端口号输入框
    private JButton sendBT;					//发送按钮
    private JButton clearBT;  				//清除按钮
    private DatagramSocket datagramSocket;	//发送数据用的载体

    /*在构造方法中完成界面初始化，Socket连接初始化，和建立监听状态*/
    public udpChat() {
        setUPUI();
        initSocket();
        setListener();
    }
    /*在 main方法中创建一个udpChat类的对象，便是运行了程序；*/
    public static void main(String [] args) {
        new udpChat();
    }

    /*初始化聊天界面*/
    private void setUPUI() {   //聊天页面初始化
        setTitle("udpChat---by dhw");					       //窗口标题栏设置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		           //窗口关闭时触发动作：退出程序
        setSize(400, 400);								   //窗口默认尺寸设置
        setResizable(false);									       //窗口大小不允许改动
        setLocationRelativeTo(null); 						           //窗口居中

        //设置窗口中的North部分
        stateLB = new JLabel("当前还未启动监听");
        stateLB.setHorizontalAlignment(JLabel.RIGHT);

        //窗口中的Center部分
        centerTextArea = new JTextArea();
        centerTextArea.setEditable(false);
        centerTextArea.setBackground(new Color(211, 211, 211));

        //窗口中的South部分
        sourthPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(5,20);
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        ipTextField = new JTextField("127.0.0.1",8);
        remotePortTF = new JTextField(String.valueOf(DEFAULT_PORT),3);
        sendBT =new JButton("发送");
        clearBT = new JButton("清屏");
        bottomPanel.add(ipTextField);
        bottomPanel.add(remotePortTF);
        bottomPanel.add(sendBT);
        bottomPanel.add(clearBT);

        sourthPanel.add(new JScrollPane(inputTextArea),BorderLayout.CENTER);
        sourthPanel.add(bottomPanel,BorderLayout.SOUTH);

        add(stateLB,BorderLayout.NORTH);
        add(new JScrollPane(centerTextArea), BorderLayout.CENTER);
        add(sourthPanel, BorderLayout.SOUTH);
        setVisible(true);

    }

    private void setListener() {//为按钮添加监听事件
        sendBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //获取发送的目标IP和端口号
                final String ipAddress = ipTextField.getText();
                final String remotePort = remotePortTF.getText();
                //判断IP地址和端口号是否为空
                if(ipAddress == null ||ipAddress.trim().equals("")
                        ||remotePort == null || remotePort.trim().equals("")){
                    JOptionPane.showMessageDialog(udpChat.this, "请输入IP地址和端口号");
                    return;
                }
                if(datagramSocket == null || datagramSocket.isClosed()) {
                    JOptionPane.showMessageDialog(udpChat.this, "监听失败");
                    return;
                }

                //获取需要发送的内容
                String sendContent = inputTextArea.getText();
                byte[] buf = sendContent.getBytes();
                try {
                    //将发送的内容显示到自己的聊天记录中
                    centerTextArea.append("我向" + ipAddress + ":" +
                            remotePort + "发送 \n" + inputTextArea.getText() +"\n\n" );
                    //添加内容后，使滚动条自动滚动到最低端
                    centerTextArea.setCaretPosition(centerTextArea.getText().length());
                    //发送数据
                    DatagramPacket packet = new DatagramPacket(buf,buf.length,
                            InetAddress.getByName(ipAddress),Integer.parseInt(remotePort));
                    datagramSocket.send(packet);
                    inputTextArea.setText("");
                }catch(IOException e1) {
                    JOptionPane.showMessageDialog(udpChat.this, "出错，本次发送失败");
                    e1.printStackTrace();
                }

            }
        });

        //为ClearBT 按钮添加事件监听器
        clearBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                centerTextArea.setText(" ");
            }
        });
    }


    private void initSocket() {
        int port = DEFAULT_PORT;
        while(true) {
            try {
                if(datagramSocket != null && !datagramSocket.isClosed()) {
                    datagramSocket.close();
                }
                try {
                    //判断端口号范围
                    port = Integer.parseInt(JOptionPane.showInputDialog(this,
                            "请输入端口号","端口号",JOptionPane.QUESTION_MESSAGE));
                    if(port <1 || port >65535) {
                        throw new RuntimeException("端口号超出范围" + port);
                    }
                }catch(Exception e) {
                    JOptionPane.showMessageDialog(null, "你输入的端口号不正确" );
                    continue;
                }

                //启动DataGramSocket
                datagramSocket = new DatagramSocket(port);
                startListen();

                stateLB.setText("正在监听端口号：" + port);
                break;

            }catch(SocketException e) {
                JOptionPane.showMessageDialog(this, "端口号已经被占用，请重新输入" );
                stateLB.setText("当前还未启动监听");
            }
        }
    }


    private void startListen() { //创建线程进行消息接收
        new Thread() {
            private DatagramPacket datagramPacket;
            public void run() {
                byte [] buf = new byte[1024];
                //创建数据包
                datagramPacket = new DatagramPacket(buf, buf.length);
                while(!datagramSocket.isClosed()) {
                    try {
                        datagramSocket.receive(datagramPacket);
                        centerTextArea.append(datagramPacket.getAddress().getHostAddress()
                                + ":" + ((InetSocketAddress) datagramPacket.getSocketAddress()
                        ).getPort() +"向我发送： \n" + new String(datagramPacket.getData(),0,datagramPacket.getLength())
                                +"\n\n" );
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

