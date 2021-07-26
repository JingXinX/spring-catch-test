import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制
 * 正常的锁(concurrency.locks或synchronized锁)在任何时刻都只允许一个任务访问一项资源，而 Semaphore允许n个任务同时访问这个资源。
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);

        for(int i = 1 ; i <= 6 ; i ++){
            new Thread(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"抢到线程锁");
                try {
                    TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"释放锁");
                semaphore.release();
            },String.valueOf(i)).start();
        }
    }
}
