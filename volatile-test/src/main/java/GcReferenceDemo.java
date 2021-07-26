import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * 场景：假如有一个应用需要读取大量的本地图片
 *
 * 如果每次读取图片都从硬盘读取则会严重影响性能
 * 如果一次性全部加载到内存中，又可能造成内存溢出
 * 此时使用软引用可以解决这个问题。
 *
 * 设计思路：使用HashMap来保存图片的路径和相应图片对象关联的软引用之间的映射关系，在内存不足时，JVM会自动回收这些缓存图片对象所占的空间，从而有效地避免了OOM的问题
 */
public class GcReferenceDemo {
    public static void main(String[] args) throws InterruptedException {
//        memorySaze();
        /*softRefMemoryEnough();
        softRefMemoryNoEnough();*/

//        weakReference();
        referenceQueue();
    }

    //回收前需要被引用的，用队列保存下
    public static void referenceQueue(){
        Object o1 = new Object();

        // 创建引用队列
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

        // 创建一个弱引用
        WeakReference<Object> weakReference = new WeakReference<>(o1, referenceQueue);

        System.out.println(o1);
        System.out.println(weakReference.get());
        // 取队列中的内容
        System.out.println(referenceQueue.poll());

        System.out.println("==================");

        o1 = null;
        System.gc();
        System.out.println("执行GC操作");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(o1);
        System.out.println(weakReference.get());
        // 取队列中的内容
        System.out.println(referenceQueue.poll());
        System.out.println(referenceQueue.poll()+"-------");//再获取就为null了

    }

    /**
     * 弱引用
     * 对于只有弱引用的对象来说，只要垃圾回收机制一运行不管JVM的内存空间是否足够，都会回收该对象占用的内存
     */
    public static void weakReference(){
        Object o1 = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(o1);
        System.out.println(o1);
        System.out.println(weakReference.get());
        o1 = null;
        System.gc();//如果这行被注释，即GC没有进行回收则弱引用能打印出值
        System.out.println(o1+"=====");
        System.out.println(weakReference.get());
    }


    /**
     * 内存够用的时候
     * -XX:+PrintGCDetails
     */
    public static void softRefMemoryEnough() {
        // 创建一个强应用
        Object o1 = new Object();
        // 创建一个软引用
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        o1 = null;
        // 手动GC
        System.gc();

        System.out.println(o1);
        System.out.println(softReference.get());//内存够用没有被回收
    }

    /**
     * JVM配置，故意产生大对象并配置小的内存，让它的内存不够用了导致OOM，看软引用的回收情况
     * -Xms5m -Xmx5m -XX:+PrintGCDetails
     */
    public static void softRefMemoryNoEnough() {

        System.out.println("========================");
        // 创建一个强应用
        Object o1 = new Object();
        // 创建一个软引用
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        o1 = null;

        // 模拟OOM自动GC
        try {
            // 创建30M的大对象
            byte[] bytes = new byte[30 * 1024 * 1024];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(o1);
            System.out.println(softReference.get());//系统内存不足。被回收结果为null
        }
    }









    private static void memorySaze() {
        // 返回Java虚拟机中内存的总量
        long totalMemory = Runtime.getRuntime().totalMemory();

        // 返回Java虚拟机中试图使用的最大内存量
        long maxMemory = Runtime.getRuntime().maxMemory();

        System.out.println(String.format("TOTAL_MEMORY(-Xms): %d B, %.2f MB.", totalMemory, totalMemory / 1024.0 / 1024));
        System.out.println(String.format("MAX_MEMORY(-Xmx): %d B, %.2f MB.", maxMemory, maxMemory / 1024.0 / 1024));
    }
}

