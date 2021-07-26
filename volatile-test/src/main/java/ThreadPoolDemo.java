import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.*;

/**
 * 第四种获取线程方式，线程池
 * 线程池7大参数：
 * corePoolSize：线程池中的常驻核心线程数
 * 在创建了线程池后，当有请求任务来之后，就会安排池中的线程去执行请求任务，近似理解为今日当值线程。
 * 当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中。
 *
 * maximumPoolSize：线程池能够容纳同时执行的最大线程数，此值必须大于等于1
 *
 * keepAliveTime：多余的空闲线程的存活时间。
 * 当前线程池数量超过corePoolSize时，当空闲时间达到keepAliveTime值时，多余空闲线程会被销毁直到只剩下corePoolSize个线程为止
 * unit：keepAliveTime的单位。
 *
 * workQueue：任务队列，被提交但尚未被执行的任务。
 *
 * threadFactory：表示生成线程池中工作线程的线程工厂，用于创建线程一般用默认的即可。
 *
 * handler：拒绝策略，表示当队列满了并且工作线程大于等于线程池的最大线程数（ maximumPoolSize)。
 */
public class ThreadPoolDemo {
    public static void main(String[] args) {
        //创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        //newFixedThreadPool创建的线程池corePoolSize和maximumPoolSize值是相等的，它使用的LinkedBlockingQueue。
//        ExecutorService threadPool = Executors.newFixedThreadPool(3);//执行长期的任务，性能会好很多

         //创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序执行。
         //newSingleThreadExecutor将corePoolSize和maximumPoolSize都设置为1，使用的LinkedBlockingQueue
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();//一个任务一个任务执行的场景

        //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
        //newCachedThreadPool将corePoolSize设置为0，将maximumPoolSize设置为Integer.MAX_VALUE，使用的SynchronousQueue，也就是说来了任务就创建线程运行，当线程空闲超过60秒，就销毁线程
        //ExecutorService threadPool = Executors.newCachedThreadPool();//执行很多短期异步的小程序或负载较轻的服务器

        //以上是JDK自带线程池（工作中都不用）用自定义线程池
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,//核心线程数
                5,//最大线程数--->核心线程数+阻塞队列数
                1L,//多于的线程存活的时间
                TimeUnit.SECONDS,//存活时间的单位
                new LinkedBlockingQueue<>(3),//阻塞队列数
                Executors.defaultThreadFactory(),//用于创建线程
                //new ThreadPoolExecutor.AbortPolicy()//拒绝策略-->超过最大线程数无法处理时报错RejectedExecutionException
                //new ThreadPoolExecutor.CallerRunsPolicy()//"调用者运行" 的一种调节机制，将某些多余的任务回退到调用者，从而降低新任务的流量
//                new ThreadPoolExecutor.DiscardOldestPolicy()//抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务
                new ThreadPoolExecutor.DiscardPolicy()//直接丢弃任务，不予任何处理也不抛出异常

        );


        try {
            for (int i = 0; i < 15; i++) {
                int num = i;
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName()+"处理----"+num);
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            threadPool.shutdown();
        }
    }


    /**
     * 线程池配置合理线程数
     *
     * CPU密集型
     *
     * CPU密集的意思是该任务需要大量的运算，而没有阻塞，CPU一直全速运行。
     *
     * CPU密集任务只有在真正的多核CPU上才可能得到加速(通过多线程),
     * 而在单核CPU上，无论你开几个模拟的多线程该任务都不可能得到加速，因为CPU总的运算能力就那些。
     *
     * CPU密集型任务配置尽可能少的线程数量：
     *
     * 一般公式：（CPU核数+1）个线程的线程池
     *
     * lO密集型
     *
     * 由于IO密集型任务线程并不是一直在执行任务，则应配置尽可能多的线程，如CPU核数 * 2。
     *
     * IO密集型，即该任务需要大量的IO，即大量的阻塞。
     *
     * 在单线程上运行IO密集型的任务会导致浪费大量的CPU运算能力浪费在等待。
     *
     * 所以在IO密集型任务中使用多线程可以大大的加速程序运行，即使在单核CPU上，这种加速主要就是利用了被浪费掉的阻塞时间。
     *
     * IO密集型时，大部分线程都阻塞，故需要多配置线程数：
     *
     * 参考公式：CPU核数/ (1-阻塞系数)
     *
     * 阻塞系数在0.8~0.9之间
     *
     * 比如8核CPU：8/(1-0.9)=80个线程数
     */
}
