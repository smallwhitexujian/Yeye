package com.framework.socket.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	
	 private ExecutorService mPool = Executors.newFixedThreadPool(2);
	 private CountDownLatch mCountDownLatch;
	 
	 private static ThreadPool instance = null;
	 private ThreadPool(){
	 }
	 
	 public static ThreadPool getInstance(){
		 if(instance == null){
			 synchronized (ThreadPool.class) {
				if(instance == null){
					instance = new ThreadPool();
				}
			}
		 }
		 return instance;
	 }
	 
	 
	 public void run(Runnable task){
		 mPool.execute(task);
	 }
	
	 
	 public ExecutorService getThreadPool(){
		 return mPool;
	 }
	
	

}
