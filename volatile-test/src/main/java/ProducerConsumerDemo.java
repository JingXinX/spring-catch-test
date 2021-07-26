import org.springframework.util.StringUtils;

import java.sql.Time;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 1生产者消费者模式
 *    传统版（synchronized, wait, notify）
 *    阻塞队列版（lock, await, signal）
 * 2线程池
 * 3消息中间件
 */



//传统模式的资源类---------------------------------------------------------------------
class TraditionData {
    private int num = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    /**
     * 生产者
     * @throws Exception
     */
    public void product() throws Exception{
        lock.lock();
        try {
            //多线程的判断一定要用while不能用if
            while (0 != num){//num不是0---有数据则等待
                condition.await();
            }
            num++;//生产数据
            System.out.println(Thread.currentThread().getName()+"生产了数据"+num);
            condition.signalAll();//通知唤醒
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }
    public void consum(){
        lock.lock();
        try {
            while (0 == num){//num是0---无数据则等待
                condition.await();
            }
            num--;//消耗数据
            System.out.println(Thread.currentThread().getName()+"消耗，数据还剩"+num);
            condition.signalAll();//通知唤醒
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}

/**
 * 阻塞队列版（lock, await, signal）------------------------------------------------------------------------------
 */
class BlockQueueVersionData {
    private volatile Boolean FLAG = true;
    private AtomicInteger atomicInteger = new AtomicInteger();
    BlockingQueue<String> blockingQueue = null;

    //带参(BlockingQueue)构造方法，这里没有指定具体的（如ArrayBlockingQueue等）BlockingQueue为一个接口interface，
    //为的是可灵活使用，使用时可传任意BlockingQueue
    public BlockQueueVersionData(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        //打印出日志，以便知道接收到的是哪个BlockingQueue
        System.out.println(blockingQueue.getClass().getName());
    }

    /**
     * 生产者方法
     * @throws Exception
     */
    public void prod()throws Exception{
        String data = null;
        boolean relValue;
        while (FLAG){
            data = atomicInteger.getAndIncrement()+"";
            //两秒钟生产一个
            relValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if(relValue){
                System.out.println(Thread.currentThread().getName()+"\t加入队列"+data+"成功");
            }else {
                System.out.println(Thread.currentThread().getName()+"\t 加入队列失败");
            }
            try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

        }
        System.out.println(Thread.currentThread().getName()+"\t 所有生产线程停止，任务本次结束(FLAG = false)");
    }

    /**
     * 消费者方法
     */
    public void consum()throws Exception{
        String pollValue;
        while (FLAG){
            pollValue = blockingQueue.poll(2, TimeUnit.SECONDS);
            if (!StringUtils.isEmpty(pollValue)) {
                System.out.println(Thread.currentThread().getName()+"\t 队列消费"+pollValue+"成功");
            }else{
                FLAG = false;
                System.out.println(Thread.currentThread().getName()+"\t 消费队列失败,队列为空，退出");
                return;
            }

        }
    }
    public void stop(){
        System.out.println(Thread.currentThread().getName()+"\t 所有线程停止！");
        this.FLAG = false;
    }
}

/**
 * 生产者消费者模式
 *    传统版（synchronized, wait, notify）
 *    阻塞队列版（lock, await, signal）
 * 线程池
 * 消息中间件
 */
public class ProducerConsumerDemo {
    public static void main(String[] args){
//        traditionVersion();
        blockingQueueVersion();

    }

    /**
     * 阻塞队列版本--------------------------------------------------------------------------------------------
     */
    private static void blockingQueueVersion(){
        BlockQueueVersionData blockQueueVersionData = new BlockQueueVersionData(new ArrayBlockingQueue(100));
        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t 生产线程启动。。。");
                blockQueueVersionData.prod();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }

        },"prod").start();
        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t 消费线程启动");
                System.out.println("\n");
                blockQueueVersionData.consum();

            } catch (Exception e) {
                e.printStackTrace();
            }

        },"consum").start();
        try {TimeUnit.SECONDS.sleep(8);} catch (InterruptedException e) {e.printStackTrace();}
        System.out.println("\n");
        blockQueueVersionData.stop();
    }

    /**
     * 传统版本---------------------------------------------------------------------------------------------------------
     */
    private static void traditionVersion() {
        TraditionData pubData = new TraditionData();
        //生产的线程
        for(int i = 1 ; i <= 5 ; i ++){
            new Thread(() -> {
                try {
                    pubData.product();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            },"AAA").start();
        }
        //消耗的线程
        for(int i = 1 ; i <= 5 ; i ++){
            new Thread(() -> {
                try {
                    pubData.consum();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            },"BBB").start();
        }
    }




}
