package com.framework.socket;

import com.framework.socket.model.SocketConfig;
import com.framework.socket.protocol.Protocol;
import com.framework.socket.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class SocketRequest {


    public byte[] request(SocketConfig socketConfig,byte[] requestParcel){
        Socket mSocket = null;
        InputStream mInputStream = null;
        OutputStream mOutputStream = null;
        byte[] result = null;
        ByteArrayOutputStream out = null;
        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(socketConfig.getHost(),socketConfig.getPort()),socketConfig.getTimeout());
            mSocket.setSoTimeout(socketConfig.getTimeout());
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mOutputStream.write(requestParcel);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            while(mInputStream.read(buffer) > 0){
                out.write(buffer);
            }
            out.flush();
            result = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
                if (mInputStream != null) {
                    mInputStream.close();
                    mInputStream = null;
                }
                if (mOutputStream != null) {
                    mOutputStream.close();
                    mOutputStream = null;
                }
                if (mSocket != null) {
                    mSocket.close();
                    mSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

	public byte[] requestRead(Protocol protocol, SocketConfig socketConfig,byte[] requestParcel) {
		Socket mSocket = null;
		InputStream mInputStream = null;
		OutputStream mOutputStream = null;
		byte[] result = null;
		int MAX_BORDER = 10 * 1024 * 1024;
		ByteArrayBuffer mByteBuffer = null;
		boolean mSwitch = true;

		try {
			mSocket = new Socket();
			mSocket.connect(new InetSocketAddress(socketConfig.getHost(),socketConfig.getPort()), socketConfig.getTimeout());
			mSocket.setSoTimeout(socketConfig.getTimeout());
			mInputStream = mSocket.getInputStream();
			mOutputStream = mSocket.getOutputStream();
			mOutputStream.write(requestParcel);
			
			// 等待收到的数据
			while (mSwitch) {
				if (mByteBuffer == null) {
					mByteBuffer = new ByteArrayBuffer(protocol.getHeadLen());
				}
				int count = mByteBuffer.mlen;
				int readCount = mByteBuffer.moffset;// 已经成功读取的字节的个数
				while (readCount < mByteBuffer.mlen) {
					int readNum = mInputStream.read(mByteBuffer.mbuffer,readCount, count - readCount);
					if (readNum < 0) {
						mSwitch = false;
						break;
					}
					if (readNum > 0) {
						readCount += readNum;
						mByteBuffer.flush(readCount);
					}
				}
				int totalLen = protocol.getDataLen(mByteBuffer.mbuffer);
				if (mByteBuffer.mlen < mByteBuffer.moffset) {
					mSwitch = false;
					mByteBuffer = null;
				} else {
					if (mByteBuffer.mlen == totalLen) {
						result = mByteBuffer.mbuffer;
						mSwitch = false;
						mByteBuffer = null;
					} else {
						if (mByteBuffer.mlen < MAX_BORDER&& totalLen < MAX_BORDER) {
							mByteBuffer.reSize(totalLen, mByteBuffer);
						}
						// oom异常处理
						else {
							mSwitch = false;
							mByteBuffer = null;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			mSwitch = false;
			mByteBuffer = null;
			return null;
		} finally {
			try {
				if (mInputStream != null) {
					mInputStream.close();
					mInputStream = null;
				}
				if (mOutputStream != null) {
					mOutputStream.close();
					mOutputStream = null;
				}
				if (mSocket != null) {
					mSocket.close();
					mSocket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSwitch = false;
			mByteBuffer = null;
		}
		return result;
	}

}
