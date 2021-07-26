import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


public class CountDownLatchCyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
//        countDownLatch();


        CyclicBarrier cyclicBarrier = new CyclicBarrier(10,()-> {
            System.out.println("线程全体执行完毕");
        });

        for(int i = 1 ; i <= 10 ; i ++){
            final int temp = i;
            new Thread(() -> {
                System.out.println("线程"+temp+"执行完毕");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }
//        System.out.println(Thread.currentThread().getName()+"执行！！");
    }

    /**
     * CyclicBarrier的字面意思就是可循环（Cyclic）使用的屏障（Barrier）。它要求做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活，线程进入屏障通过CyclicBarrier的await方法。
     *
     * CyclicBarrier与CountDownLatch的区别：CyclicBarrier可重复多次，而CountDownLatch只能是一次。
     */



    /**
     * 让一线程阻塞直到另一些线程完成一系列操作才被唤醒。
     *
     * CountDownLatch主要有两个方法（await()，countDown()）。
     *
     * 当一个或多个线程调用await()时，调用线程会被阻塞。其它线程调用countDown()会将计数器减1(调用countDown方法的线程不会阻塞)，当计数器的值变为零时，因调用await方法被阻塞的线程会被唤醒，继续执行。
     */
    private static void countDownLatch() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i = 1 ; i <= 10 ; i ++){
            final int temp = i;
            new Thread(() -> {
                System.out.println("线程"+temp+"执行完毕");
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName()+"执行！！");
    }
}
