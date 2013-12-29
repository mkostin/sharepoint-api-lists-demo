package com.microsoft.opentech.office.odata.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Handler;
import android.os.Looper;

/**
 * Implementation of a <i>listenable</i> {@link Future}.
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

	private Handler handler = new Handler(Looper.getMainLooper());
	
	/**
	 * Creates a new instance of OfficeFuture class.
	 * 
	 * @param callback Callback to be invoked when future ends.
	 */
	public OfficeFuture(ICallback<V> callback) {
	    mCallback = callback;
	}

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
	void setResult(V result) {
		synchronized (mDoneLock) {
			mResult = result;
			mIsDone = true;
			if (mCallback != null) {
                invokeDone(result);
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

	void triggerError(Throwable error) {
	    synchronized (mErrorLock) {
            if (mCallback != null) {
                invokeError(error);
            }

            mError = error;
            mResultSemaphore.release();
        }
	}

	/**
	 * Invokes {@link ICallback#onDone(Object)} callback on a UI thread.
	 *
	 * @param result Execution result.
	 */
    protected void invokeDone(final V result) {
        handler.post(new Runnable(){
            @Override
             public void run() {
                mCallback.onDone(result);
             }
        });
    }

    /**
     * Invokes {@link ICallback#onError(Throwable)} callback on a UI thread.
     *
     * @param error Execution error.
     */
    protected void invokeError(final Throwable error) {
        handler.post(new Runnable(){
           @Override
            public void run() {
               mCallback.onError(error);
            }
        });
    }
}
