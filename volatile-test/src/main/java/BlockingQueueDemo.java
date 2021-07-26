import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ArrayBlockingQueue：由数组结构组成的有界阻塞队列。
 * LinkedBlockingQueue：由链表结构组成的有界（但大小默认值为Integer.MAX_VALUE）阻塞队列。
 * PriorityBlockingQueue：支持优先级排序的无界阻塞队列。
 * DelayQueue：使用优先级队列实现妁延迟无界阻塞队列。
 * SynchronousQueue：不存储元素的阻塞队列。
 * LinkedTransferQueue：由链表结构绒成的无界阻塞队列。
 * LinkedBlockingDeque：由链表结构组成的双向阻塞队列。
 *
 *
 * 方法类型	抛出异常	    特殊值	   阻塞	    超时
 * 插入  	add(e)  	offer(e)	put(e)	offer(e,time,unit)
 * 移除  	remove()	poll()  	take()	poll(time,unit)
 * 检查  	element()	peek()	    不可用	不可用
 *
 * 性质	               说明
 * 抛出异常  当阻塞队列满时：在往队列中add插入元素会抛出 IIIegalStateException：Queue full
 *          当阻塞队列空时：再往队列中remove移除元素，会抛出NoSuchException
 * 特殊性  插入方法，成功true，失败false
 *        移除方法：成功返回出队列元素，队列没有就返回空
 * 一直阻塞 当阻塞队列满时，生产者继续往队列里put元素，队列会一直阻塞生产线程直到put数据or响应中断退出。
 *         当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用。
 * 超时退出  当阻塞队列满时，队里会阻塞生产者线程一定时间，超过限时后生产者线程会退出
 */

public class BlockingQueueDemo {
    public static void main(String[] args) {

        BlockingQueue blockingQueue = new ArrayBlockingQueue(3);
//    抛异常
        System.out.println(blockingQueue.add(1));//true...
        System.out.println(blockingQueue.add(2));
        System.out.println(blockingQueue.add(3));
//        System.out.println(blockingQueue.add(4));//超出设置的队列大小则抛异常：java.lang.IllegalStateException: Queue full


        System.out.println(blockingQueue.element());//检查到下一项：1
        System.out.println(blockingQueue.remove());//1
        System.out.println(blockingQueue.remove());//2
        System.out.println(blockingQueue.element());//3
        System.out.println(blockingQueue.remove());//3
//        System.out.println(blockingQueue.remove());//超出设置的队列大小则抛异常：java.util.NoSuchElementException
//        System.out.println(blockingQueue.element());//队列中没有元素时则抛异常：java.util.NoSuchElementException

//=============================返回布尔值==================================================================
        System.out.println(blockingQueue.offer("a"));//true..
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));
        System.out.println(blockingQueue.offer("d"));//超出设置的队列大小时返回false

        System.out.println(blockingQueue.peek());//检查到下一项：a
        System.out.println(blockingQueue.poll());//a
        System.out.println(blockingQueue.poll());//b
        System.out.println(blockingQueue.poll());//c
        System.out.println(blockingQueue.poll());//获取超出队列大小时返回null

        System.out.println(blockingQueue.peek());//超出设置的队列大小时返回null

//=============================阻塞==================================================================
            new Thread(()->{
                try {
                    System.out.println("---------put--------");
                    blockingQueue.put(1);
                    blockingQueue.put(2);
                    blockingQueue.put(3);
                    blockingQueue.put(4);
                } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }).start();


        try {
            TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}


        try {
            System.out.println("start take");
            System.out.println("take "+blockingQueue.take());
            System.out.println("take "+blockingQueue.take());
            System.out.println("take "+blockingQueue.take());
            System.out.println("take "+blockingQueue.take());
//            System.out.println("take "+blockingQueue.take());//如果take不到就会一直阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//=============================阻塞==================================================================
        try {
            System.out.println(blockingQueue.offer("a", 2L, TimeUnit.SECONDS));//true
            System.out.println(blockingQueue.offer("b", 2L, TimeUnit.SECONDS));
            System.out.println(blockingQueue.offer("c", 2L, TimeUnit.SECONDS));//true
            System.out.println(blockingQueue.offer("d", 2L, TimeUnit.SECONDS));//false


            System.out.println(blockingQueue.poll(2L, TimeUnit.SECONDS));
            System.out.println(blockingQueue.poll(2L, TimeUnit.SECONDS));
            System.out.println(blockingQueue.poll(2L, TimeUnit.SECONDS));
            System.out.println(blockingQueue.poll(2L, TimeUnit.SECONDS));//null
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
