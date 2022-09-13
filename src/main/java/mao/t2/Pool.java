package mao.t2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Project name(项目名称)：java并发编程_Semaphore
 * Package(包名): mao.t2
 * Class(类名): Pool
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/13
 * Time(创建时间)： 18:53
 * Version(版本): 1.0
 * Description(描述)： 用 Semaphore 实现简单连接池。线程数和数据库连接数是相等的
 */

public class Pool
{
    /**
     * 池大小
     */
    private final int poolSize;

    /**
     * 连接对象数组
     */
    private final Connection[] connections;

    /**
     * 连接状态数组 0 表示空闲， 1 表示繁忙
     */
    private final AtomicIntegerArray states;

    /**
     * 信号量
     */
    private final Semaphore semaphore;

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(Pool.class);

    /**
     * 构造方法
     *
     * @param poolSize 池大小
     */
    public Pool(int poolSize)
    {
        if (poolSize <= 0)
        {
            throw new IllegalArgumentException("poolSize must >0");
        }
        this.poolSize = poolSize;
        this.semaphore = new Semaphore(poolSize);
        this.connections = new Connection[poolSize];
        this.states = new AtomicIntegerArray(new int[poolSize]);
        for (int i = 0; i < poolSize; i++)
        {
            //connections[i] = new MockConnection("连接" + (i + 1));
        }
    }

    /**
     * 借连接
     *
     * @return {@link Connection}
     */
    public Connection borrow()
    {
        try
        {
            //获取许可
            semaphore.acquire();
            for (int i = 0; i < poolSize; i++)
            {
                //遍历是否有空闲的连接
                if (states.get(i) == 0)
                {
                    while (true)
                    {
                        if (states.compareAndSet(i, 0, 1))
                        {
                            log.debug("获取连接：" + connections[i]);
                            return connections[i];
                        }
                        Thread.yield();
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 归还连接
     *
     * @param connection 连接
     */
    public void free(Connection connection)
    {
        try
        {
            for (int i = 0; i < poolSize; i++)
            {
                if (connections[i] == connection)
                {
                    states.set(i, 0);
                    log.debug("归还连接：" + connection);
                    break;
                }
            }
        }
        finally
        {
            //释放许可
            semaphore.release();
        }


    }

}


/*
class MockConnection implements Connection
{
    //todo
}
*/
