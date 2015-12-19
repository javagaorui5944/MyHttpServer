package com.gaorui.MyHttpServer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyHttpServer {
	// 服务器根目录，我放了静态和图片在win7计算机路径下
	public static String WEB_ROOT = "D:/MyServer";
	// 端口
	private int port;
	// 用户请求的文件的url


	public MyHttpServer(String root, int port) {
		WEB_ROOT = root;
		this.port = port;
	
	}



	class ServerThread implements Runnable {
		ServerSocket serverSocket;
		private String requestPath;
		// 处理GET请求
		private void doGet(DataInputStream reader, OutputStream out)
				throws Exception {
			if (new File(WEB_ROOT + this.requestPath).exists()) {
				// 从服务器根目录下找到用户请求的文件并发送回浏览器
				InputStream fileIn = new FileInputStream(WEB_ROOT
						+ this.requestPath);
				System.out.println("WEB_ROOT + this.requestPath:" + WEB_ROOT
						+ this.requestPath);
				System.out.println("fileIn.available():" + fileIn.available());
				byte[] buf = new byte[fileIn.available()];
				fileIn.read(buf);
				out.write(buf);

				out.close();
				fileIn.close();
				reader.close();

				System.out.println("request complete.");

			}
		}
		public ServerThread(ServerSocket serverSocket,String requestPath) {
			this.serverSocket = serverSocket;
			this.requestPath  = requestPath;
		}

		@Override
		public void run() {
			while(true){
			try {
				Socket socket = serverSocket.accept();
				
				System.out.println("new request coming.");
				DataInputStream reader = new DataInputStream(
						(socket.getInputStream()));
				String line = reader.readLine();
				if (line != null) {

					String method = line.substring(0, 4).trim();
					OutputStream out = socket.getOutputStream();
					requestPath = line.split(" ")[1];
					System.out.println("this.requestPath:" + requestPath);
					if ("GET".equalsIgnoreCase(method)) {
						System.out.println("do get......");
						this.doGet(reader, out);

					}

				} else {
					System.err.println("line is null!");
				}
				socket.close();
				//System.out.println("socket closed.");
			} catch (Exception e) {
				e.printStackTrace();
				}
			}
		}
	}

	public void service() throws Exception {
		ServerSocket serverSocket = new ServerSocket(this.port);
		System.out.println("server is ok.");
		
		// 开启serverSocket等待用户请求到来，然后根据请求的类别作处理
		// 在这里我只针对GET和POST作了处理
		ServerThread st = new ServerThread(serverSocket,"");
		Thread t = new Thread(st);
		t.start();
		
	}

	public static void main(String[] args) {
		MyHttpServer mhs = new MyHttpServer("D:/MyServer", 7224);
		try {
			mhs.service();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
