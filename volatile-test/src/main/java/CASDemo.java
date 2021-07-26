import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS核心类：Unsafe类，java无法直接访问底层系统，需要通过本地方法（native）来访问。通过Unsafe类可以直接操作特定内存的数据，存在与java的sun.misc包中，几乎所有方法都是native的
 * CAS全称compare And Swap比较并交换，是一条CPU原语，汇编指令实现原子操作。也就是说CAS是一条CPU的原子指令不会造成所谓的数据不一致的问题
 * unsafe类加CPU原语
 */
public class CASDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5,6)+"--------"+atomicInteger);
        System.out.println(atomicInteger.compareAndSet(5,7)+"--------"+atomicInteger);
    }


}
