package mao.t1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * Project name(项目名称)：java并发编程_Semaphore
 * Package(包名): mao.t1
 * Class(类名): Test
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/13
 * Time(创建时间)： 18:42
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class Test
{
    /**
     * 信号量
     */
    private static final Semaphore semaphore = new Semaphore(3);

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args)
    {
        for (int i = 0; i < 20; i++)
        {
            int finalI = i;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        //获取一个许可
                        semaphore.acquire();
                        log.debug("线程" + (finalI + 1) + "开始运行");
                        Thread.sleep(1000);
                        log.debug("线程" + (finalI + 1) + "运行结束");
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        //释放许可
                        semaphore.release();
                    }
                }
            }, "t" + (i + 1)).start();
        }
    }
}
