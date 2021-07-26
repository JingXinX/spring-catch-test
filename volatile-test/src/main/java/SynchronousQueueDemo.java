import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue没有容量。
 *
 * 与其他BlockingQueue不同，SynchronousQueue是一个不存储元素的BlockingQueue。
 *
 * 每一个put操作必须要等待一个take操作，否则不能继续添加元素，反之亦然。
 */
public class SynchronousQueueDemo {
    public static void main(String[] args) {
        BlockingQueue blockingQueue = new SynchronousQueue();
        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+" put a");
                blockingQueue.put("a");
                System.out.println(Thread.currentThread().getName()+" put b");
                blockingQueue.put("b");
                System.out.println(Thread.currentThread().getName()+" put c");
                blockingQueue.put("c");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"线程1").start();


        new Thread(()->{
            try {
                    TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName()+" take "+blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName()+" take "+blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName()+" take "+blockingQueue.take());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"线程2").start();

    }
}
