package com.will.common.tool.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTool {
	
	
	
	public static void createAppCacheFile(Context ctx,String filename){
		String cacheDir = ctx.getCacheDir().getAbsolutePath();
		File fileInfo = new File(cacheDir+"/"+filename);
		try {
			mkFile(fileInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createAppFile(Context ctx,String filename){
		String fileDir = ctx.getFilesDir().getAbsolutePath();
		File fileInfo = new File(fileDir+"/"+filename);
		try {
			mkFile(fileInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//不允许删除到/mnt/sdcard这层目录
	public static void clearSdcardDir(File path){
		if(path == null){
			throw new NullPointerException();
		}
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String sdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
			String pathStr = path.getAbsolutePath();
			if(pathStr.equals(sdcardRoot) || !pathStr.startsWith(sdcardRoot)){
				return;
			}
			clearWholeDir(path,pathStr);
		}
	}
	
	public static void clearAppCahces(Context ctx){
		File cacheDir = ctx.getCacheDir();
		clearWholeDir(cacheDir,cacheDir.getAbsolutePath());
	}
	
	public static void clearAppFiles(Context ctx){
		File fileDir = ctx.getFilesDir();
		clearWholeDir(fileDir,fileDir.getAbsolutePath());
	}
	
	public static boolean deleteFile(File path){
		if(path == null){
			return false;
		}
		if(path.exists()){
			return path.delete();
		}
		else{
			return false;
		}
	}
	
	
	//删除整个目录下的文件包含目录，除了当前目录，root乱用会出现删除不了
	private static void clearWholeDir(File path,String root){
		if(path.isDirectory()){
			File[] childs = path.listFiles();
			if(childs != null){
				for(File child:childs){
					clearWholeDir(child,root);
				}
			}
		}
		if(!path.getAbsolutePath().equals(root)){
			path.delete();
		}
	}
	
	//创建文件，先创建目录，然后再创建文件，文件已经存在 就不创建文件
	public static void mkFile(File fileInfo) throws IOException {
		File parent = fileInfo.getParentFile();
		if (!parent.exists()) {
			mkDir(parent);
		}
		if(!fileInfo.exists()){
			fileInfo.createNewFile();
		}
	}
	
	//保存图片到本地
	public static void savePic(Bitmap bitmap,CompressFormat type,int quality,String picPath){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(bitmap != null && picPath != null){
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(picPath);
					//Bitmap.CompressFormat.JPEG
					if (bitmap.compress(type, quality, fos)) {
						fos.flush();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					bitmap.recycle();
					bitmap = null;
					if(fos != null){
						try {
							fos.close();
							fos = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}
	}
	
	
	
	//创建目录，如果已经存在目录则不创建
	public static void mkDir(File dirInfo){
		File parent = dirInfo.getParentFile();
		if (!parent.exists()) {
			mkDir(parent);
		}
		if(!dirInfo.exists()){
			dirInfo.mkdir();
		}
	}
	
	public static String getSDCardPath(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}


}
