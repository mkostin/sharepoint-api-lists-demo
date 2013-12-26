package com.microsoft.opentech.office.odata.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents long running SignalR operations
 */
public class OfficeFuture<V> implements Future<V> {
	boolean mIsCancelled = false;
	boolean mIsDone = false;
	private V mResult = null;
	private Object mDoneLock = new Object();
	private Object mErrorLock = new Object();
	private Throwable mError = null;
	private ICallback<V> mCallback;
	
	private Semaphore mResultSemaphore = new Semaphore(0);
	
	/**
	 * Cancels the operation
	 */
	public void cancel() {
		mIsCancelled = true;
		mResultSemaphore.release();
	}
	
	/**
	 * Sets a result to the future and finishes its execution
	 * @param result The future result
	 */
	public void setResult(V result) {
		synchronized (mDoneLock) {
			mResult = result;
			mIsDone = true;
			if (mCallback != null) {
                mCallback.onDone(result);
			}
		}
		
		mResultSemaphore.release();
	}
	
	/**
	 * Indicates if the operation is cancelled
	 * @return True if the operation is cancelled
	 */
	public boolean isCancelled() {
		return mIsCancelled;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		cancel();
		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		try {
			return get(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (mResultSemaphore.tryAcquire(timeout, unit)) {
			if (mError != null) {
				throw new ExecutionException(mError);
			} else {
				return mResult;
			}
		} else {
			throw new TimeoutException();
		}
	}
	
	@Override
	public boolean isDone() {
		return mIsDone;
	}
	
	public OfficeFuture<V> setCallback(ICallback<V> callback) {
        synchronized (mDoneLock) {
            mCallback = callback;
            
            if (isDone()) {
                try {
                    callback.onDone(get());
                } catch (Exception e) {
                    triggerError(e);
                }
            }
        }
        
        return this;
    }
	
	public void triggerError(Throwable error) {
	    synchronized (mErrorLock) {
            if (mCallback != null) {
                mCallback.onError(error);
            }
            
            mError = error;
            mResultSemaphore.release();
        }
	}
}
