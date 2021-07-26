#导引
GC算法（引用计数、复制、标记清除、标记压缩整理）是内存回收的方法论，垃圾收集器就是方法论的落地实现

因为目前为止还没有完美的收集器出现，更加没有万能的收集器，只是针对具体应用最合适的收集器，进行分代收集

4种主要垃圾收集器
* Serial 串行垃圾回收器：它为单线程环境设计且值使用一个线程进行垃圾收集，会暂停所有的用户线程，只有当垃圾回收完成时，才会重新唤醒主线程继续执行。所以不适合服务器环境
* Parallel 并行： 多个垃圾收集线程并行工作，此时用户线程也是阻塞的，适用于科学计算 / 大数据处理等弱交互场景，也就是说Serial 和 Parallel其实是类似的，不过是多了几个线程进行垃圾收集，但是主线程都会被暂停，但是并行垃圾收集器处理时间，肯定比串行的垃圾收集器要更短
* CMS 并发(并发标记清除ConcMarkSweep)：用户线程和垃圾收集线程同时执行（不一定是并行，可能是交替执行），不需要停顿用户线程，互联网公司都在使用，适用于响应时间有要求的场景。
* G1：G1垃圾回收器将堆内存分割成不同的区域然后并发的对其进行垃圾回收
###如何查看默认的垃圾收集器
java -XX:+PrintCommandLineFlags -version
输出结果：
-XX:InitialHeapSize=266930432 -XX:MaxHeapSize=4270886912 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers
 -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
 
 从结果看到-XX:+UseParallelGC，也就是说默认的垃圾收集器是并行垃圾回收器
 ###JVM默认的垃圾收集器有哪些
 Java中一共有7大垃圾收集器
 
 年轻代GC
 
* UserSerialGC：串行垃圾收集器
* UserParallelGC：并行垃圾收集器
* UseParNewGC：年轻代的并行垃圾回收器

老年代GC
* UserSerialOldGC：串行老年代垃圾收集器（已经被废除）
* UseParallelOldGC：老年代的并行垃圾回收器
* UseConcMarkSweepGC：（CMS）并发标记清除

 老嫩通吃
* UseG1GC：G1垃圾收集器
##GC约定参数说明
* DefNew：Default New Generation
* Tenured：Old
* ParNew：Parallel New Generation
* PSYoungGen：Parallel Scavenge
* ParOldGen：Parallel Old Generation