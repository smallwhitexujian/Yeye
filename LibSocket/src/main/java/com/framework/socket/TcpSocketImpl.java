package com.framework.socket;


import android.util.Log;

import com.framework.socket.heartbeat.Heartbeat;
import com.framework.socket.model.SocketConfig;
import com.framework.socket.out.TcpSocketCallback;
import com.framework.socket.protocol.Protocol;
import com.framework.socket.thread.ThreadPool;
import com.framework.socket.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * 收发包。Tcp Socket
 */
public class TcpSocketImpl implements TcpSocket{

	protected Socket mSocket;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    protected volatile boolean mRun;
    protected boolean isLostConnect = true;
    protected int mRunStatus = CONNECTNULL;

    protected TcpSocketCallback mTcpSocketCallback;
    protected Protocol mProtocol;
    protected SocketConfig mSocketConfig;

    protected int MAX_BORDER = 10 * 1024 * 1024;
    protected int SLEEP_TIME = 250;
    
    protected Heartbeat mHeartbeat;
    

    public TcpSocketImpl(SocketConfig socketConfig,Protocol protocol,TcpSocketCallback tcpSocketCallback){
        mRunStatus = CONNECTINIT;
        mProtocol = protocol;
        mSocketConfig = socketConfig;
        mTcpSocketCallback = tcpSocketCallback;
    }

    @Override
    public void disconnect() {
    	if(mHeartbeat != null){
    		mHeartbeat.doneHeartbeat();
    		mHeartbeat = null;
    	}
        try {
            if (mInputStream != null) {
                mInputStream.close();
//                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
//                mOutputStream = null;
            }
            if (mSocket != null) {
                mSocket.close();
//                mSocket = null;
            }
            mRun = false;
            mRunStatus = CONNECTLOST;
        } catch (IOException e) {
            e.printStackTrace();
            mRunStatus = CONNECTNULL;
        }
    }

    @Override
    public boolean connect() {
        mRunStatus = CONNECTING;
        if (mSocketConfig == null) {
            mRunStatus = CONNECTFAILD;
            return false;
        }
        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(mSocketConfig.getHost(), mSocketConfig.getPort()),mSocketConfig.getTimeout());
            mSocket.setSoTimeout(mSocketConfig.getTimeout());
            Log.e("=====",mSocketConfig.getTimeout()+"====timeout====");
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mRunStatus = CONNECTED;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            mRunStatus = CONNECTFAILD;
        }
        return false;
    }

    @Override
    public void onLostConnect() {
        disconnect();
        if(mTcpSocketCallback != null){
        	mTcpSocketCallback.onLostConnect();
        }
    }

    public void registerCallback(TcpSocketCallback callback){
    	mTcpSocketCallback = callback;

    }
    
    
    

    @Override
    public void recv(){
        if(mInputStream == null){
            return;
        }
        ThreadPool.getInstance().run(new Thread(){
        	@Override
        	public void run() {
        		 ByteArrayBuffer mByteBuffer = null;
                 mRun = true;
                 isLostConnect = false;
                 while (mRun) {
                     try {
                         if (mByteBuffer == null) {
                             mByteBuffer = new ByteArrayBuffer(mProtocol.getHeadLen());
                         }
                         int count = mByteBuffer.mlen;
                         int readCount = mByteBuffer.moffset;// 已经成功读取的字节的个数
                         while (readCount < mByteBuffer.mlen) {
                             int readNum = mInputStream.read(mByteBuffer.mbuffer, readCount, count - readCount);
                             if (readNum < 0) {
                                 mRun = false;
                                 isLostConnect = true;
                                 break;
                             }
                             if (readNum > 0) {
                                 readCount += readNum;
                                 mByteBuffer.flush(readCount);
                             }
//                             Thread.sleep(SLEEP_TIME);
                         }
                         int totalLen = mProtocol.getDataLen(mByteBuffer.mbuffer);
                         //数据包小于指定大小,丢失连接（inputsream关闭)
                         if (mByteBuffer.mlen < mByteBuffer.moffset) {
                             mRun = false;
                             mByteBuffer = null;
                             isLostConnect = true;
                         } else {
                             if (mByteBuffer.mlen == totalLen) {
                                 if (mTcpSocketCallback != null) {
                                 	mTcpSocketCallback.onReceiveParcel(mByteBuffer.mbuffer);
                                 }
                                 mByteBuffer = null;
                             } else {
                                 if (mByteBuffer.mlen < MAX_BORDER && totalLen < MAX_BORDER) {
                                     mByteBuffer.reSize(totalLen, mByteBuffer);
                                 }
                                 //oom异常处理
                                 else {
                                     mRun = false;
                                     mByteBuffer = null;
                                     isLostConnect = true;
                                 }
                             }
                         }
                     }catch (IOException e){
                         if(e instanceof java.net.SocketTimeoutException){
                             continue;
                         }
                         isLostConnect = true;
                         mRun = false;
                         System.out.println("======"+e.getMessage());
                         e.printStackTrace();
                     }
                 }
                 disconnect();
                 //丢失连接业务流程回调
                 if (isLostConnect) {
                     if(mTcpSocketCallback != null){
                     	mTcpSocketCallback.onLostConnect();
                     }
                 }
                 else{
                     if(mTcpSocketCallback != null){
                     	mTcpSocketCallback.onReadTaskFinish();
                     }
                 }
             }
        });
//        ThreadPool.getInstance().run(new Runnable() {
//            @Override
//            public void run() {
//                ByteArrayBuffer mByteBuffer = null;
//                mRun = true;
//                isLostConnect = false;
//                while (mRun) {
//                    Log.e("jjfly=====",mProtocol.getHeadLen()+"");
//                    try {
//                        if (mByteBuffer == null) {
//                            mByteBuffer = new ByteArrayBuffer(mProtocol.getHeadLen());
//                            Log.e("jjfly=====",mProtocol.getHeadLen()+"");
//                        }
//                        int count = mByteBuffer.mlen;
//                        int readCount = mByteBuffer.moffset;// 已经成功读取的字节的个数
//                        while (readCount < mByteBuffer.mlen) {
//                            int readNum = mInputStream.read(mByteBuffer.mbuffer, readCount, count - readCount);
//                            if (readNum < 0) {
//                                mRun = false;
//                                isLostConnect = true;
//                                break;
//                            }
//                            if (readNum > 0) {
//                                readCount += readNum;
//                                mByteBuffer.flush(readCount);
//                            }
////                            Thread.sleep(SLEEP_TIME);
//                        }
//                        int totalLen = mProtocol.getDataLen(mByteBuffer.mbuffer);
//                        //数据包小于指定大小,丢失连接（inputsream关闭)
//                        if (mByteBuffer.mlen < mByteBuffer.moffset) {
//                            mRun = false;
//                            mByteBuffer = null;
//                            isLostConnect = true;
//                        } else {
//                            if (mByteBuffer.mlen == totalLen) {
//                                if (mTcpSocketCallback != null) {
//                                	mTcpSocketCallback.onReceiveParcel(mByteBuffer.mbuffer);
//                                }
//                                mByteBuffer = null;
//                            } else {
//                                if (mByteBuffer.mlen < MAX_BORDER && totalLen < MAX_BORDER) {
//                                    mByteBuffer.reSize(totalLen, mByteBuffer);
//                                }
//                                //oom异常处理
//                                else {
//                                    mRun = false;
//                                    mByteBuffer = null;
//                                    isLostConnect = true;
//                                }
//                            }
//                        }
//                    }catch (IOException e){
//                        if(e instanceof java.net.SocketTimeoutException){
//                            continue;
//                        }
//                        isLostConnect = true;
//                        mRun = false;
//                        e.printStackTrace();
//                    }
//                }
//                disconnect();
//                //丢失连接业务流程回调
//                if (isLostConnect) {
//                    if(mTcpSocketCallback != null){
//                    	mTcpSocketCallback.onLostConnect();
//                    }
//                }
//                else{
//                    if(mTcpSocketCallback != null){
//                    	mTcpSocketCallback.onReadTaskFinish();
//                    }
//                }
//
//            }
//        });
    }

    @Override
    public boolean send(final byte[] src,final int srcStart,final int len){
        if (mOutputStream != null) {
            try {
                mOutputStream.write(src, srcStart, len);
                return true;
            } catch (IOException e) {
                Log.e("====",e.getMessage());
                e.printStackTrace();
                onLostConnect();
            }
        }
        return false;
    }

    @Override
    public int getRunStatus(){
        return mRunStatus;
    }
    
    @Override
    public void takeCareHeartbeat(Heartbeat heartbeat) {
    	this.mHeartbeat = heartbeat;
    }
   

}