package mt4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

import engine.util.SetENVUtil;
import model.entity.macroeconomic.CountryMainIndex;
import model.entity.macroeconomic.db.CountryMainIndexSQL;
import tcc.utils.log.LogService;

public class SocketServer {
	public static final int PORT = 6000;// 监听的端口号

	public static void main(String[] args) {
		SetENVUtil.setENV();
		System.out.println("服务器启动...\n");
		SocketServer server = new SocketServer();
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

	private class HandlerThread implements Runnable {
		private Socket socket;

		public HandlerThread(Socket client) {
			socket = client;
			new Thread(this).start();
		}

		public void run() {
			try {
				// 读取客户端数据
				DataInputStream reader = new DataInputStream(
						socket.getInputStream());
				byte[] buffer = new byte[10000]; // 缓冲区的大小
				reader.read(buffer); // 处理接收到的报文，转换成字符串
				/**
				 * C++传递过来的中文字，需要转化一下。C++默认使用GBK。
				 * GB2312是GBK的子集，只有简体中文。因为数据库用GB2312，所以这里直接转为GB2312
				 * */
				String message = new String(buffer, "GB2312").trim();
//				String mesg2 = new String(buffer, "GBK");
				System.out.println("from client: " + message);
				String[] params = message.split("_");
				// int date = Integer.parseInt(params[0]);
				// int hour = Integer.parseInt(params[1]);
				String res = getTrend(params[0], params[1]);

				// 向客户端回复信息
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				String s = res;
				byte[] responseBuffer = res.getBytes("GBK");
				System.out.println("Final str to client:" + res);
				out.write(responseBuffer, 0, responseBuffer.length);
				out.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("服务器 run 异常: " + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {
						socket = null;
						System.out.println("服务端 finally 异常:" + e.getMessage());
					}
				}
			}
		}
	}

	public String getTrend(String dateStr, String hourStr)
			throws RemoteException {
		int res = 9;
		String[] dates = dateStr.split("\\.");
		if(dates[1].length()<2){
			dates[1] = "0"+dates[1];
		}
		if(dates[2].length()<2){
			dates[2] = "0"+dates[2];
		}
		String[] hours = hourStr.split(":");
		if(hours[0].length()< 2){
			hours[0] = "0" + hours[0];
		}
		if(hours[1].length()< 2){
			hours[1] = "0" + hours[1];
		}
		
		int date = Integer.parseInt(dates[0] + dates[1] + dates[2]);
		int hour = Integer.parseInt(hours[0] + hours[1]);
		
		String resDate = dates[1] + dates[2] + hours[0] + hours[1];
		try {
			List<CountryMainIndex> events = CountryMainIndexSQL.getObjs("USD",
					"EUR", "Wallstartcn", date, hour);
			if (events.size() > 0) {
				CountryMainIndex ci = events.get(0);
				String reportDateStr = "" + ci.getReportDate();
				String reportTimeStr = "" + ci.getReportHour();
				if (reportTimeStr.length() < 4) {
					reportTimeStr = "0" + reportTimeStr;
				}
				resDate = reportDateStr.substring(4, 6)
						+ reportDateStr.subSequence(6, 8)
						+ reportTimeStr.substring(0, 2)
						+ reportTimeStr.subSequence(2, 4);
				
				if (ci.getCurrency().equals("USD")) {
					if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_DOWN)) {
						// 做多
						// +200
						res = 1;
					} else if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_FLAT)) {
						res = 0;
					} else if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_UP)) {
						// 做空
						res = 2;
					}

				} else {
					if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_DOWN)) {
						// 做空
						res = 1;
					} else if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_FLAT)) {
						res = 0;
					} else if (ci.getInference().equals(
							CountryMainIndex.INFERENCE_UP)) {
						// 做多
						// + 200
						res = 2;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 1406061020
		if(resDate.length() < 8){
			resDate = "10" + resDate + res;
		}else{
			resDate = "1" + resDate + res;
		}
		LogService.msg("getTrend:" + resDate);

		return resDate;

	}

}
