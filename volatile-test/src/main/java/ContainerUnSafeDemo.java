import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class ContainerUnSafeDemo {


    public static void main(String[] args) {
//        Map<String ,String> map = Collections.synchronizedMap(new HashMap<>());
        Map<String ,String> map = new ConcurrentHashMap<>();

        //线程数多的情况，有可能出现ConcurrentModificationException--并发修改异常
        for(int i = 1 ; i < 100 ; i ++){
            new Thread(() -> {
                map.put(Thread.currentThread().getName(),UUID.randomUUID().toString().substring(0,8));
                System.out.println(map);
            },String.valueOf(i)).start();
        }
//        setUnSafe();
//        listUnSafe();
    }

    private static void setUnSafe() {
        //        Set<String> set = new HashSet<>();
        Set<String> set = new CopyOnWriteArraySet<>();
//        Set<String> set = Collections.synchronizedSet(new HashSet<>());

        //线程数多的情况，也有可能出现ConcurrentModificationException--并发修改异常
        for(int i = 1 ; i < 1000 ; i ++){
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(set);
            },String.valueOf(i)).start();
        }
    }

    private static void listUnSafe() {
        List<String> list = new ArrayList();
        List<String> s = Collections.synchronizedList(new ArrayList<>());
        List<String> l = new CopyOnWriteArrayList<>();

        //线程数多的情况，有可能出现ConcurrentModificationException--并发修改异常
        for(int i = 1 ; i < 100 ; i ++){
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
        /**
         * 一个线程在写，另一个线程抢夺，造成数据不一致出现该异常
         * 1、可以用vector，是syn的保证了一致性但并发性急剧下降
         * 2、可以用Collections.synchronizedList(new ArrayList<>())
         * 3.用CopyOnWriteArrayList  写时复制写读分离
         *    CopyOnWrite--写时复制容器，向一个容器添加元素的时候不是直接添加到该容器，而是先将当前容器进行copy，复制出一个新的容器，向新容器中添加元素
         *    然后再将原容器的引用指向新的容器。好处是可以将CopyOnWrite进行并发读写而不需要加锁，当前容器是不会添加元素的。
         */}
}
