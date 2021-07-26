import java.util.Random;

/**
 * 种垃圾收集器
 * Serial收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseSerialGC
 * ParNew收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseParNewGC
 * Parallel收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseParallelGC
 * ParallelOld收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseParallelOldGC
 * CMS收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseConcMarkSweepGC
 * SerialOld收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseSerialOldGC
 * G1收集器:-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+PrintCommandLineFlags -XX:+UseG1GC
 *
 */
public class GCRecovery {
    //默认GC配置:-XX:+UseParallelGC
    public static void main(String[] args) {
        //此代码为的是产生垃圾回收（会有java.lang.OutOfMemoryError: Java heap space）
        //修改配置为-Xms10m -Xmx10m new一个大于此配置的对象也可以 如 new byte [11*1024*1024]
        Random rand = new Random(System.nanoTime());

        try {
            String str = "Hello, GC!!";
            while(true) {
                str += str + rand.nextInt(Integer.MAX_VALUE) + rand.nextInt(Integer.MAX_VALUE);
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
