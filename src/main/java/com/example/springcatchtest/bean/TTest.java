package com.example.springcatchtest.bean;


import org.assertj.core.util.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TTest {
	private static Object oLock = new Object();
	private static Lock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();
	private static int num = 0;



	public static void main(String[] args) {

//		testObjLock();//Obj
//		testCondition();//

//		new Thread(new PrintNumThird(),"T1").start();
//		new Thread(new PrintNumThird(),"T2").start();
//		getNum2();

	}
//-------------

	static void getNum2(){
		int [] nums = new int[]{1,2,3,4,5,6,7,8};
		int target = 9;
		Map<Integer,Integer> munMap = new HashMap<>();
		int [] index=new int[]{};
		for (int i = 0; i<nums.length;i++){
			int souseNum = target - nums[i];//9-2=7
			if(munMap.containsKey(souseNum)){
				index = new int[]{i,munMap.get(souseNum)};
				for (int x: index) {
					System.out.println(nums[x]+"---->"+x);
				}
			}
			munMap.put(nums[i],i);
		}

	}

static void getNum(){
		int [] nums = new int[]{1,2,3,4,5,6,7,8};
		int target = 9;
	int [] index=new int[]{};
		for (int i = 0; i<nums.length;i++){
			for (int j = i+1 ; j<nums.length - 1;j ++){
				if(target - nums[i] == nums[j]){
					index = new int[]{i,j};
				}
			}
		}
	for (int x: index) {
		System.out.println(nums[x]+"---->"+x);
	}
}

//---------------------------------两个线程交替打印1-50--------------------------------------------------------------------
	public static class PrintNumThird implements Runnable{
		/*@Override
		public void run() {
			try {
				lock.lock();
				while (num < 50){
					System.out.println(Thread.currentThread().getName()+"print:" + ++num);
					condition.signal();
					try {
						condition.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					condition.signal();
				}
			}finally {
				lock.unlock();
			}
		}*/

		@Override
		public void run() {
			synchronized (oLock){
				while (num < 50){
					System.out.println(Thread.currentThread().getName()+"print:" + ++num);
					oLock.notifyAll();
					try {
						oLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				oLock.notifyAll();
			}
		}
	}
//-------------------------------wait/notify,await/signal------------------


	static void testObjLock(){
		new Thread(()->{
			synchronized (oLock){
				System.out.println(Thread.currentThread().getName()+"COME IN");
				try {
					oLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName()+"GET UP");
			}
		},"a").start();

		new Thread(()->{
			synchronized (oLock){
				System.out.println(Thread.currentThread().getName()+"call a");
				oLock.notifyAll();
			}
		},"b").start();
	}

	static void testCondition(){
		new Thread(()->{
			System.out.println(Thread.currentThread().getName()+"COME IN");
			lock.lock();
			try {
				condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"GET UP");
			condition.signal();
			lock.unlock();
		},"1").start();
		new Thread(()->{
			lock.lock();
			System.out.println(Thread.currentThread().getName()+"call 1");
			condition.signal();
			lock.unlock();
		},"2").start();
	}

}

