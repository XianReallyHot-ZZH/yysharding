package cn.youyou.yysharding;

/**
 * sharding的上下文
 *
 * 技巧：使用ThreadLocal+static方法
 * 效果：
 *  1、并发时，不同线程之间的对象是隔离的，不同线程之间是线程安全的；
 *  2、用static方法操作ShardingResult，方便某些不好实现入参的方法和类在编码的时候能够调用static方法，实现对对象的操作（获取，修改等）
 *
 */
public class ShardingContext {

    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get()
    {
        return LOCAL.get();
    }

    public static void set(ShardingResult shardingResult)
    {
        LOCAL.set(shardingResult);
    }

}
