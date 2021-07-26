/**
 * 高并发多线程版本单例模式：双端检索+在需要单例的对象前加volatile
 */
public class SingletonDemo {
   private volatile static SingletonDemo instance = null;//加volatile禁止指令重排

    private SingletonDemo() {
        System.out.println(Thread.currentThread().getName()+"构造方法");
    }
    //DCL （双端检索 double check lock）也不一定线程安全，因为会有指令重排
    //原因：某一个线程执行第一次检查时读到的instance！=null，但instance的引用对象却还没有初始化完成（instance = new new SingletonDemo()还没有完成。过程可分为3步
    //                                                                                1 memory=allocate（）；分配内存空间
    //                                                                                2 instance（memory）；初始化对象
    //                                                                                3 instance=memory；设置instance指向刚分配的内存地址，此时instance！=null
    //                                                                                 而2、3步没有依赖关系由于指令重排也许3先执行，造成线程安全问题）
    public static  SingletonDemo getInstance(){
        if(instance == null){
            synchronized (SingletonDemo.class){
                if(null == instance){
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }


    public static void main(String[] args) {
        //单线程全部都是true
//        System.out.println(getInstance()==getInstance());
//        System.out.println(SingletonDemo.getInstance()==SingletonDemo.getInstance());
//        System.out.println(SingletonDemo.getInstance()==getInstance());
        //多线程情况
        for(int i = 1 ; i < 10 ; i ++){
            new Thread(() -> {
                SingletonDemo.getInstance();
            },String.valueOf(i)).start();
        }
    }
}
