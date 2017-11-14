package com.WMSLib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 将输入流转换成字符串
	 * @param is 输入流
	 * @return 字符串  返回null表示异常
	 */
	public static String streamToString(InputStream is) {
		//定义字节队列输出流对象
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		//定义一个字节数组，用于读取存放数据
		byte[] buffer=new byte[1024];
		//定义读取节点标示
		int temp=-1;
		try {
			while((temp=is.read(buffer))!=-1){
				bos.write(buffer, 0, temp);
			}
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
