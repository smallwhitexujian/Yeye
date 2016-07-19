package com.framework.socket.util;


public class ByteUtil {

	private static final int INT32_LEN = Integer.SIZE;
	private static final int INT64_LEN = Long.SIZE;
	private static final int INT16_LEN = Short.SIZE;
	private static final int INT8_LEN = Byte.SIZE;
	
	public static final int BIG_ENDIAN = 0;
	public static final int SMALL_ENDIAN = 1;
	

	public static int[] INT64_2_INT32(long num){
		int len = INT64_LEN / INT32_LEN;
		int[] data = new int[len];
		for (int i=0;i<len;i++){
			data[i] = (int) (num >>((len - 1 -i)*INT32_LEN)&0xFFFFFFFF);
		}
		
		return data;
	}
	
	public static short[] INT32_2_INT16(int num){
		int len = INT32_LEN / INT16_LEN;
		short[] data = new short[len];
		for (int i=0;i<len;i++){
			data[i] = (short) (num >>((len - 1 -i)*INT16_LEN)&0xFFFF);
		}
		
		return data;
	}
	
	//默认转换出来的都为大端数据
	public static byte[] INT64_2_INT8(long num,int endianType){
		int len = INT64_LEN / INT8_LEN;
		byte[] data = new byte[len];
		if(endianType == SMALL_ENDIAN){
			for (int i=len-1;i>=0;i--){
				data[i] = (byte) (num >>(i*INT8_LEN)&0xFF);
			}
		}
		else{
			for (int i=0;i<len;i++){
				data[i] = (byte) (num >>((len - 1 -i)*INT8_LEN)&0xFF);
			}
		}
		return data;
	}
	
	//默认转换出来的都为大端数据
	public static byte[] INT32_2_INT8(int num,int endianType){
		int len = INT32_LEN / INT8_LEN;
		byte[] data = new byte[len];
			if(endianType == SMALL_ENDIAN){
				for (int i=len-1;i>=0;i--){
					data[i] = (byte) (num >>(i*INT8_LEN)&0xFF);
				}
			}
			else{
				for (int i=0;i<len;i++){
					data[i] = (byte) (num >>((len - 1 -i)*INT8_LEN)&0xFF);
				}
			}
		return data;
	}
	
	//默认转换出来的都为大端数据
	public static byte[] INT16_2_INT8(short num,int endianType){
		int len = INT16_LEN / INT8_LEN;
		byte[] data = new byte[len];
			if(endianType == SMALL_ENDIAN){
				for (int i=len-1;i>=0;i--){
					data[i] = (byte) (num >>(i*INT8_LEN)&0xFF);
				}
			}
			else{
				for (int i=0;i<len;i++){
					data[i] = (byte) (num >>((len - 1 -i)*INT8_LEN)&0xFF);
				}
			}
		
		return data;
	}
	
	/**
	 * byte[] 转换成 byte,int short,long
	 * @param data
	 * @param offset
	 * @return
	 * 注：data[]数据分大小端来处理
	 * 大端模式，是指数据的高位，保存在内存的低地址中
	 * 小端模式，是指数据的高位，保存在内存的高地址中
	 */
	public static long bytes2Long(byte[] data,int offset,int endianType){
		int len = INT64_LEN/INT8_LEN;
		if(data == null || data.length < offset+len){
			throw new IllegalAccessError();
		}
		long value = 0;
		for (int i = 0 ; i < len ; i++) {
			if(endianType == SMALL_ENDIAN){
				value |= (data[offset + len-1-i] & 0xFF);
			}
			else if(endianType == BIG_ENDIAN){
				value |= (data[offset + i] & 0xFF);
			}
			if( i == len-1){
				break;
			}
			value = value << INT8_LEN;
		}
		return value;
	}
	
	
	public static int bytes2Int(byte[] data,int offset,int endianType){
		int len = INT32_LEN/INT8_LEN;
		if(data == null || data.length < offset+len){
			throw new IllegalAccessError();
		}
		int value = 0;
		for (int i = 0 ; i < len ; i++) {
			if(endianType == SMALL_ENDIAN){
				value |= (data[offset + len-1-i] & 0xFF);
			}
			else if(endianType == BIG_ENDIAN){
				value |= (data[offset + i] & 0xFF);
			}
			if( i == len-1){
				break;
			}
			value = value << INT8_LEN;
		}
		return value;
	}
	
	public static short bytes2Short(byte[] data,int offset,int endianType){
		int len = INT16_LEN/INT8_LEN;
		if(data == null || data.length < offset+len){
			throw new IllegalAccessError();
		}
		short value = 0;
		for (int i = 0 ; i < len ; i++) {
			if(endianType == SMALL_ENDIAN){
				value |= (data[offset + len-1-i] & 0xFF);
			}
			else if(endianType == BIG_ENDIAN){
				value |= (data[offset + i] & 0xFF);
			}
			if( i == len-1){
				break;
			}
			value = (short) (value << INT8_LEN);
		}
		return value;
	}
	
	public static byte bytes2byte(byte[] data,int offset){
		int len = INT8_LEN/INT8_LEN;
		if(data == null || data.length < offset+len){
			throw new IllegalAccessError();
		}
		return data[offset];
	}
	
	public static byte[] bytes2bytes(byte[] data,int offset,int len,int srcEndianType,int dstEndianType){
		if(data == null || data.length < offset+len){
			throw new IllegalAccessError();
		}
		byte[] newData = endianSwap(data,srcEndianType,dstEndianType);
		byte[] value = new byte[len];
		System.arraycopy(newData, 0, value, 0, len);
		return value;
	}
	
	
	//大小端转化
	public static byte[] endianSwap(byte[] src,int srcType,int dstType){
		if(src == null){
			return null;
		}
		if(srcType != dstType){
			int len = src.length;
			byte[] dst = new byte[len];
			for(int i=0;i<len;i++){
				dst[i] = src[len-1-i];
			}
			return dst;
		}
		return src;
	}
	
	//转化成16进制
	public static String bytes2Hex(byte[] bytes){
	    StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X ", b));
	    }
	   return sb.toString();
	}
	
}
