import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Send{

    //synchronized是可重入锁
    public synchronized void sendMs(){
        sendEmail();
        System.out.println(Thread.currentThread().getName()+"发短信方法");
    }
    public synchronized void sendEmail(){
        System.out.println(Thread.currentThread().getName()+"发邮件");
    }

}
class Phone implements Runnable{
    Lock lock = new ReentrantLock();
    public void getLock(){
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName()+"getLock");
            setLock();
        }finally {
            lock.unlock();
        }


    }

    public void setLock(){
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName()+"setLock");
        }finally {
            lock.unlock();
        }

    }
    @Override
    public void run() {
        getLock();
    }
}

public class ReenterLockDemo {


    public static void main(String[] args) {
        Phone phone = new Phone();

        //Phone实现了Runnable接口
       Thread t1 = new Thread(phone,"t1");
       Thread t2 = new Thread(phone,"t2");
       t1.start();
       t2.start();



        /*Send send = new Send();
        new Thread(()->{
            send.sendMs();
        },"AAA").start();

        new Thread(() -> {
            send.sendMs();
        },"BBB").start();*/
    }


}
