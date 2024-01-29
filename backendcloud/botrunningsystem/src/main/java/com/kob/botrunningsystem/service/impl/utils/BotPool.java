package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition(); //条件变量
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();//被唤醒的进程在阻塞处继续执行
            //这两个要放在一起，因为bot添加成功后才能唤醒线程
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot){
        //为了防止用户写死循环之类，导致卡死，所以新开一个线程
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot); //2s，总共5s，一个bot给2s
    }

    @Override
    public void run() { //消息队列大致是这样实现的
        //生产者-消费者模型
        //如果队列是空的，那么这个线程阻塞，一旦有新的任务进来，它会被唤醒
        while (true){
            lock.lock(); //由于生产者不断往队列里加任务，而当前线程又不断执行任务，有冲突
            if (bots.isEmpty()) {
                try {
                    condition.await(); //await后会自动释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();//有异常的话，可能会没有解锁
                    break;
                }
            } else {
                Bot bot = bots.remove();//返回并删除队头
                lock.unlock();
                consume(bot); //比较耗时，放到unlock后面
            }
        }
    }
}
