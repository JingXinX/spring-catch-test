import java.util.concurrent.TimeUnit;

/**
 *
 */
class ThreadLock implements Runnable{
    private String lockA;
    private String lockB;

    public ThreadLock(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }


    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+"\t 持有"+lockA);
            try {
                TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}

            synchronized (lockB){
                System.out.println(Thread.currentThread().getName()+"\t 持有"+lockB);
            }
        }
    }
}


public class DeadLockDemo {


    public static void main(String[] args) {
        String lockA = "lockAm";
        String lockB = "lockBm";
        new Thread(new ThreadLock(lockA,lockB),"T1").start();
        new Thread(new ThreadLock(lockB,lockA),"T2").start();

    }
}
