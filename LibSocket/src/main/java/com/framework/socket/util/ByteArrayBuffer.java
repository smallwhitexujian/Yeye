//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.framework.socket.util;

public class ByteArrayBuffer {
    public byte[] mbuffer;
    public int moffset;
    public int mlen;
    public int mrealsize;

    public ByteArrayBuffer(int len) {
        if (len < 0) {
            throw new IllegalArgumentException();
        }
        mbuffer = new byte[len];
        moffset = 0;
        mlen = len;
        mrealsize = 0;
    }

    public ByteArrayBuffer(int len, ByteArrayBuffer oldBuffer) {
        if (oldBuffer == null || oldBuffer.mbuffer == null || len < 0) {
            throw new IllegalArgumentException();
        }
        int size = len + oldBuffer.mlen;
        mbuffer = new byte[size];
        System.arraycopy(oldBuffer.mbuffer, 0, mbuffer, 0, oldBuffer.mlen);
        moffset = len;
        mrealsize = len;
        mlen = size;
    }

    public int append(byte[] data) {
        if (data == null) {
            return -1;
        }
        System.arraycopy(data, 0, mbuffer, moffset, data.length);
        moffset += data.length;
        mrealsize = moffset;
        return moffset;
    }

    public ByteArrayBuffer append(int readLean,ByteArrayBuffer oldBuffer){
        ByteArrayBuffer newBuffer = new ByteArrayBuffer(oldBuffer.mlen+readLean);
        System.arraycopy(oldBuffer.mbuffer, 0, newBuffer.mbuffer, 0, oldBuffer.moffset);
        newBuffer.moffset = oldBuffer.moffset;
        newBuffer.mrealsize = oldBuffer.mrealsize;
        return newBuffer;
    }

    public int flush(int position) {
        moffset = position;
        return moffset;
    }

    public void reSize(int totalSize, ByteArrayBuffer buffer) {
        byte[] data = buffer.mbuffer;
        buffer.mbuffer = new byte[totalSize];
        System.arraycopy(data, 0, buffer.mbuffer, 0, buffer.moffset);
        buffer.mlen = totalSize;
    }
}
