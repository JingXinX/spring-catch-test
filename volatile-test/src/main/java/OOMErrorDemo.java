import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JVM两种常见的错误
 * StackOverFlowError:java.lang.StackOverflowError---如：不断的方法递归（方法的引用在栈中）不断的深度加载（栈的大小512~1024兆）栈被撑爆
 * OutOfMemoryError:
 * java.lang.OutOfMemoryError：java heap space  堆空间错误---不断的new对象如：string的不断+字符串，会不断的new新对象（new对象在堆中），或者new了一个大于内存（-Xms和-Xmx设置比较小）的对象
 * java.lang.OutOfMemoryError：GC overhead limit exceeeded GC回收时间过长，超出GC开销限制
 * java.lang.OutOfMemoryError：Direct buffer memory
 * java.lang.OutOfMemoryError：unable to create new native thread
 * java.lang.OutOfMemoryError：Metaspace
 *
 */
public class OOMErrorDemo {
    public static void main(String[] args) {

//        oomGCOverHeadLimitExeceed();
//        directBufferMemory();
//        unableCreateThread();
//        oomMetaSpace(args);
        heapSpace();

    }
    //静态内部类
static class OOmMeatSpaceObject{}
    /**
     * 使用java -XX:+PrintFlagsInitial命令查看本机的初始化参数，-XX:MetaspaceSize为21810376B（大约20.8M）
     * Java 8及之后的版本使用Metaspace来替代永久代。
     * Metaspace是方法区在Hotspot 中的实现，它与持久代最大的区别在于：Metaspace并不在虚拟机内存中而是使用本地内存也即在Java8中,
     * classe metadata(the virtual machines internal presentation of Java class)，被存储在叫做Metaspace native memory。
     *
     * 永久代（Java8后被原空向Metaspace取代了）存放了以下信息：
     * 虚拟机加载的类信息
     * 常量池
     * 静态变量
     * 即时编译后的代码
     *
     * 模拟Metaspace空间溢出，我们借助CGLib直接操作字节码运行时不断生成类往元空间灌，类占据的空间总是会超过Metaspace指定的空间大小的。
     * -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
     */
    public static void oomMetaSpace(String[] args){
        // 模拟计数多少次以后发生异常
        int i = 0;
        try {
                while (true){
                    i++;
                    //使用Spring的动态字节码技术,不停的创建上面静态内部类（静态的属于所有类的模板和原版 ，不停向meatSpace加载）
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(OOmMeatSpaceObject.class);
                    enhancer.setUseCache(false);//不用缓存
                    enhancer.setCallback(new MethodInterceptor() {//回调函数用方法的拦截器
                        @Override
                        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                            return methodProxy.invoke(0,args);//通过反射不停的产生OOmMeatSpaceObject对象
                        }
                    });
                    enhancer.create();
                }
                }catch (Exception e){
            System.out.println("发生异常的次数："+i);
                    e.printStackTrace();
                }

    }


    /**
     * 不能够创建更多的新的线程了，也就是说创建线程的上限达到了
     * 高并发请求服务器时，经常会出现异常java.lang.OutOfMemoryError:unable to create new native thread，准确说该native thread异常与对应的平台有关
     *
     * 导致原因：
     * 应用创建了太多线程，一个应用进程创建多个线程，超过系统承载极限
     * 服务器并不允许你的应用程序创建这么多线程，linux系统默认运行单个进程可以创建的线程为1024个，如果应用创建超过这个数量，就会报 java.lang.OutOfMemoryError:unable to create new native thread
     * 解决方法：
     * 想办法降低你应用程序创建线程的数量，分析应用是否真的需要创建这么多线程，如果不是，改代码将线程数降到最低
     * 对于有的应用，确实需要创建很多线程，远超过linux系统默认1024个线程限制，可以通过修改Linux服务器配置，扩大linux默认限制
     *
     * 非root用户登录Linux系统（CentOS）测试
     * 服务器级别调参调优
     * 查看系统线程限制数目
     * ulimit -u
     * 修改系统线程限制数目
     * vim /etc/security/limits.d/90-nproc.conf
     * 打开后发现除了root，其他账户都限制在1024个
     * 而root用户： root soft nproc unlimited
     * 如果想更改其他用户线程数则修改数量即可
     */
    public static void unableCreateThread(){
        for (int i = 0;  ; i++) {
            System.out.println("==================i:"+i);
            try {TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);} catch (InterruptedException e) {e.printStackTrace();}
            new Thread(()->{}).start();
            
        }
    }

    /**
     * -Xms5m -Xmx5m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
     * 导致原因：
     *
     * 写NIO程序经常使用ByteBuffer来读取或者写入数据，这是一种基于通道(Channel)与缓冲区(Buffer)的IO方式，它可以使用Native函数库直接分配堆外内存，然后通过一个存储在Java堆里面的DirectByteBuffer对象作为这块内存的引用进行操作。这样能在一些场景中显著提高性能，因为避兔了在Java堆和Native堆中来回复制数据。
     *
     * ByteBuffer.allocate(capability) 第一种方式是分配VM堆内存，属于GC管辖范围，由于需要拷贝所以速度相对较慢。
     * ByteBuffer.allocateDirect(capability) 第二种方式是分配OS本地内存，不属于GC管辖范围，由于不需要内存拷贝所以速度相对较快。
     * 但如果不断分配本地内存，堆内存很少使用，那么JV就不需要执行GC，DirectByteBuffer对象们就不会被回收，这时候堆内存充足，但本地内存可能已经使用光了，再次尝试分配本地内存就会出现OutOfMemoryError，那程序就直接崩溃了。
     */
    public static void directBufferMemory(){
        System.out.println("maxDirectMemory配置:"+sun.misc.VM.maxDirectMemory()/1024/1024);
        try {
            TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(6*1024*1024);

    }

    /**
     * -Xms10m -Xmx10m -XX:MaxDirectMemorySize=5m -XX:+PrintGCDetails
     * GC回收时间过长时会抛出OutOfMemroyError。过长的定义是，超过98%的时间用来做GC并且回收了不到2%的堆内存，连续多次GC 都只回收了不到2%的极端情况下才会抛出。
     * 假如不抛出GC overhead limit错误会发生什么情况呢？那就是GC清理的这么点内存很快会再次填满，迫使Gc再次执行。这样就形成恶性循环，CPU使用率一直是100%，而Gc却没有任何成果
     */
    public static void oomGCOverHeadLimitExeceed(){
        List<String> list = new ArrayList<>();
        int i = 0;
        try {
            while (true){
                list.add(String.valueOf(++i).intern());
            }
        }catch (Exception e){
            System.out.println("***************i:" + i);
            e.printStackTrace();
            throw e;
        }
    }
    //-Xms10m -Xmx10m -XX:+PrintGCDetails
    public static void heapSpace(){
        byte[] b = new byte[11*1024*1024];
    }
}
