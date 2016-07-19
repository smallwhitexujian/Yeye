package com.will.web;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class HttpPostFile {
    /**
     *
     * @param urlStr  上传地址
     * @param postParams  post 参数
     * @param filesParams key=表单文件的key,value=文件全路径
	 *
     * @return
     */
	public static String uploadFile(String urlStr, Map<String,String> postParams, Map<String,String> filesParams) {
		try {
			// 换行符
			final String newLine = "\r\n";
			final String boundaryPrefix = "--";
			// 定义数据分隔线
			String BOUNDARY = "========7d4a6d158c9";
			// 服务器的域名
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置为POST情
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求头参数
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			//form 数据信息
			for (Entry<String, String> entry : postParams.entrySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(boundaryPrefix);
				sb.append(BOUNDARY);
				sb.append(newLine);
				sb.append("Content-Disposition: form-data; name=" + "\"" + entry.getKey() + "\"" + newLine);
				sb.append(newLine);
				sb.append(entry.getValue());
				sb.append(newLine);
				out.write(sb.toString().getBytes());
			}

			//文件数据信息
			for (Entry<String, String> entry : filesParams.entrySet()) {
				File file = new File(entry.getValue());
				StringBuilder sb = new StringBuilder();
				sb.append(boundaryPrefix);
				sb.append(BOUNDARY);
				sb.append(newLine);
				// 文件参数,photo参数名可以随意修改
				sb.append("Content-Disposition: form-data;name=" + "\"" + entry.getKey() + "\";filename=\"" + file.getName() + "\"" + newLine);
				sb.append("Content-Type:application/octet-stream" + newLine);
				sb.append(newLine);

				// 将参数头的数据写入到输出流中
				out.write(sb.toString().getBytes());

				// 数据输入流,用于读取文件数据
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				byte[] bufferOut = new byte[1024];
				int bytes = 0;
				// 每次读1KB数据,并且将文件数据写入到输出流中
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				// 最后添加换行
				out.write(newLine.getBytes());
				in.close();
			}
//			 定义最后数据分隔线，即--加上BOUNDARY再加上--。
			byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
			// 写上结尾标识
			out.write(end_data);
			out.flush();
			out.close();
			// 定义BufferedReader输入流来读取URL的响应
			StringBuilder resultBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				resultBuilder.append(line);
			}
			reader.close();
			return new String(resultBuilder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
