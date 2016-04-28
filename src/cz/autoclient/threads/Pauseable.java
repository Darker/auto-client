/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.threads;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Utility class to pause and unpause threads
 * with Java Concurrency
 * @author Martin Braun
 */
public abstract class Pauseable extends Thread {
    private Lock mLock;
    private Condition mCondition;
    private AtomicBoolean mAwait;

    public Pauseable(String threadName) {
      super(threadName);
      initLocks();
    }
    public Pauseable() {
      super();
      initLocks();
    }
    private void initLocks() {
      this.mLock = new ReentrantLock();
      this.mCondition = this.mLock.newCondition();
      this.mAwait = new AtomicBoolean(false); 
    }

    /**
     * waits until the threads until this.mAwait is set to true
     * @throws InterruptedException
     */
    protected void waitPause() throws InterruptedException {
        while(this.mAwait.get()) {
            this.mLock.lock();
            try {
                this.mCondition.await();
            } finally {
                this.mLock.unlock();
            }
        }
    }

    /**
     * pauses or unpauses
   * @param pValue
     */
    public void pause(boolean pValue) {
        if(!pValue){
            this.mLock.lock();
            try {
                this.mCondition.signalAll();
            } finally {
                this.mLock.unlock();
            }
        }
        this.mAwait.set(pValue);
    }

}

