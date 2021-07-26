import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile 低配版的同步机制
 * 多线程之间的可见性
 * volatile  保证可见性
 *           不保证原子性
 *           禁止指令重排(有序)：多线程环境会有指令重排情况（计算机在执行程序时为提高性能，编译器和处理器常会对指令做重排）
 *                       多线程环境线程交替执行，由于编译器优化重排的存在，多个线程中使用的变量能否保证一致性无法保证，结果也无法预测
 *                       volatile加入内存屏障，在屏障前后禁止指令重排序后话。可禁止重排
 *
 *                       单线程是没有这个问题的，单线程环境里是确保程序执行最终结果和代码顺序执行的结果一致
 */






public class VolatileDemo {

//    volatile int num = 0;//加volatile后

        int num = 0;
    public void numIs100() {
        num = 100;
    }

    public  void addPlusPlus() {
        num++;
    }
    AtomicInteger atomicInteger = new AtomicInteger();
    public void addAromic(){
        atomicInteger.getAndIncrement();//每次加一
    }
}
    class testClass {

        public static void main(String[] args) {
            //可见性
//            volatileSeeOk();
            atmc();
        }

        private static void atmc() {
            VolatileDemo volatileDemo = new VolatileDemo();
            for (int i = 1; i <= 20; i++) {
                new Thread(() -> {
                    for (int j = 1; j <= 1000; j++) {
                        volatileDemo.addPlusPlus();
                        volatileDemo.addAromic();
                    }
                }, String.valueOf(i)).start();
            }
            while (Thread.activeCount() > 2) {//大于2，因为默认后台有两个线程，一个是main线程一个是GC线程，大于2说明其他的线程还没有执行完
                Thread.yield();//礼让线程先不执行
            }
            System.out.println(volatileDemo.num);//不是原子性的，结果不是20*1000---java底层的汇编
            System.out.println(volatileDemo.atomicInteger);//解决了原子性问题----底层原理CAS
        }


        private static void volatileSeeOk() {
            VolatileDemo volatileDemo = new VolatileDemo();//new时在主内存堆num为0，之后A线程和main线程分别复制num=0到自己的工作内存中（有的地方叫做栈空间）
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "开始");
                try { TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}

                volatileDemo.numIs100();
                System.out.println(Thread.currentThread().getName() + "睡3秒后修改num为" + volatileDemo.num);
            }, "A").start();

            //第二个线程--main线程
            while (volatileDemo.num == 0) {//线程A将num修改为100但是对main线程不可见，main线程获取的num依然是0
                //这里获取的num是0，什么也不做，程序卡在这里,下边那句话无法打印
            }
            System.out.println(Thread.currentThread().getName() + "线程获取到num为" + volatileDemo.num);//加volatile后 此处得意打印

//      =============================================================================================
            new Thread(() -> {
//            try {TimeUnit.SECONDS.sleep(4);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println(Thread.currentThread().getName() + "获取num为：" + volatileDemo.num);
            }, "B").start();
        }
    }


