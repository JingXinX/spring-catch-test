import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 * 写完释放锁之后读取的线程才能占有锁
 * 独占锁：指该锁一次只能被一个线程所持有。对ReentrantLock和Synchronized而言都是独占锁
 *
 * 共享锁：指该锁可被多个线程所持有。
 *
 * 多个线程同时读一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行。但是，如果有一个线程想去写共享资源来，就不应该再有其它线程可以对该资源进行读或写。
 *
 * 对ReentrantReadWriteLock其读锁是共享锁，其写锁是独占锁。
 *
 * 读锁的共享锁可保证并发读是非常高效的，读写，写读，写写的过程是互斥的
 */
class MyCache{
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private volatile Map<String,Object> map = new HashMap();

	public void put(String key,Object obj){
		//加锁
		readWriteLock.writeLock().lock();
		System.out.println(Thread.currentThread().getName()+"正在写入");
		map.put(key,obj);
		try {
			TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}finally {
			readWriteLock.writeLock().unlock();
		}
		System.out.println(Thread.currentThread().getName()+"写入完成");
	}
	public void get(String key){
		readWriteLock.readLock().lock();
		System.out.println(Thread.currentThread().getName()+"正在读取");
		Object res = map.get(key);
		try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}finally {
			readWriteLock.readLock().unlock();
		}
		System.out.println(Thread.currentThread().getName()+"读取完成"+res);

	}
}
public class ReadWriteLockDemo {




	public static void main(String[] args) {
		normal();

	}

	/**
	 * 没有加读写锁读取时有可能没写完，获取到null值
	 */
	private static void normal() {
		MyCache myCache = new MyCache();
		for (int i = 1; i <= 10 ;i++){
			final int tname = i;
			new Thread(()->{
				myCache.put(tname+"",tname);
			},tname+"").start();
		}
		for(int i = 1 ; i <= 10 ; i ++){
			final int key = i;
			new Thread(() -> {
				myCache.get(key+"");
		    },String.valueOf(i)).start();
		}
	}
}
