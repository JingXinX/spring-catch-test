import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

@ToString
    @Data
    @AllArgsConstructor
   class User {
        private String userName;
        private int age;

    }

    public class ABADemo{
        static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
        static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100,1);
        public static void main(String[] args) {
//            atomicReference();

            new Thread(() -> {
                // 把100 改成 101 然后在改成100，也就是ABA
                atomicReference.compareAndSet(100, 101);
                atomicReference.compareAndSet(101, 100);
            }, "t1").start();

            new Thread(() -> {
                try {
                    // 睡眠一秒，保证t1线程，完成了ABA操作
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 把100 改成 101 然后在改成100，也就是ABA
                System.out.println(atomicReference.compareAndSet(100, 2019) + "\t" + atomicReference.get());

            }, "t2").start();

		try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}


            System.out.println("============以下是ABA问题的解决==========");

            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 初始值" + atomicStampedReference.getReference());

                // 获取版本号
                int stamp = atomicStampedReference.getStamp();
                System.out.println(Thread.currentThread().getName() + "\t 第一次版本号" + stamp);

                // 暂停t3一秒钟
                try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}

                // 传入4个值，期望值，更新值，期望版本号，更新版本号
                atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(),
                        atomicStampedReference.getStamp() + 1);

                System.out.println(Thread.currentThread().getName() + "\t 第二次版本号" + atomicStampedReference.getStamp());

                atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(),
                        atomicStampedReference.getStamp() + 1);

                System.out.println(Thread.currentThread().getName() + "\t 第三次版本号" + atomicStampedReference.getStamp());

            }, "t3").start();

            new Thread(() -> {

                // 获取版本号
                int stamp = atomicStampedReference.getStamp();
                System.out.println(Thread.currentThread().getName() + "\t 第一次版本号" + stamp);

                // 暂停t4 3秒钟，保证t3线程也进行一次ABA问题
                try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}

                boolean result = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);

                System.out.println(Thread.currentThread().getName() + "\t 修改成功否：" + result + "\t 当前最新实际版本号："
                        + atomicStampedReference.getStamp());

                System.out.println(Thread.currentThread().getName() + "\t 当前实际最新值" + atomicStampedReference.getReference());

            }, "t4").start();
        }


        private static void atomicReference() {
            AtomicReference<User> atomicUser = new AtomicReference<>();
            User z3 = new User("z3",1);
            User l4 = new User("l4",2);
            atomicUser.set(z3);
            System.out.println(atomicUser.compareAndSet(z3,l4)+atomicUser.get().toString());
            System.out.println(atomicUser.compareAndSet(z3,l4)+atomicUser.get().toString());
        }
    }




