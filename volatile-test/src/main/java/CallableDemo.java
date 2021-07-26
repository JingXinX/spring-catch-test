import java.util.concurrent.*;

/**
 *获得线程的方法除了记成Thread类和实现Runnable接口外，第三种--实现Callable接口
 */
class MyCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread().getName()+"\t callable is runing");
        try {
            TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
        return 1024;//待返回值的、可抛异常的、接口实现的方法不一样
    }
}

/**
 * 当一个线程要执行多个方法时其中一个方法耗时较长，可用FutureTask（类似分支）来分别执行，最后再汇合
 */
public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask(new MyCallable());//Runnable 接口与Callable接口的中间衔接为FutuerTask
        new Thread(futureTask,"T1").start();

        Integer i = 10;//主线程的
        System.out.println(Thread.currentThread().getName()+"线程执行");

        //FutureTask-->T1线程的方法最好放到最后，因为假设T1线程的方法需要执行较长时间防止其他线程阻塞，所以将此线程的方法放最后
        Integer o =  futureTask.get();

        System.out.println(o+i);//汇合
        System.out.println(Runtime.getRuntime().availableProcessors());//查看CPU--4核

    }



}
