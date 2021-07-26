import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁
 */
public class SpinLockDemo {
	//实体类的原子引用
	AtomicReference<Thread> atomicReference = new AtomicReference<>();
	//加锁
	public void myLock(){
		Thread thread = Thread.currentThread();
		System.out.println(thread.getName()+"\t"+"进入myLock");
		//自旋加锁,期望是mull则设置当前线程
		//自旋不会产生阻塞而是采用循环的方式去获取锁,好处是减少线程上下文切换的消耗,但是比较消耗CPU
		while (!atomicReference.compareAndSet(null,thread)){
//			System.out.println(Thread.currentThread().getName()+"进入自旋");
		}
	}
	public void myUnLock(){
		Thread thread = Thread.currentThread();
		atomicReference.compareAndSet(thread,null);
		System.out.println(thread.getName()+"\t"+"unLock~~~~~");
	}

	public static void main(String[] args) {
		SpinLockDemo spinLockDemo = new SpinLockDemo();
		new Thread(()->{
			spinLockDemo.myLock();
			try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
			spinLockDemo.myUnLock();
		},"AAA").start();
		try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
		new Thread(()->{
			spinLockDemo.myLock();
			try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			spinLockDemo.myUnLock();
		},"BBB").start();
	}
}
