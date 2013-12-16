package com.microsoft.office365.sdk.http;


/**
 * Java HttpConnection implementation, based on HttpURLConnection and 
 * threads async operations
 */
public class JavaHttpConnection implements HttpConnection{
	
	/**
	 * User agent for requests
	 */
    private static final String USER_AGENT = "temporary-user-agent-java";
    
    /**
     * User agent header name
     */
    private static final String USER_AGENT_HEADER = "User-Agent";


    @Override
    public HttpConnectionFuture execute(final Request request) {
        
        request.addHeader(USER_AGENT_HEADER, USER_AGENT);
        
        final HttpConnectionFuture future = new HttpConnectionFuture();
        
        final NetworkRunnable target = new NetworkRunnable(request, future);
        
        final NetworkThread networkThread = new NetworkThread(target) {
        	@Override
        	void releaseAndStop() {
        		try {
        			target.closeStreamAndConnection();
        		} catch (Throwable error) {
        		}
        	}
        };
        
        future.onCancelled(new Runnable() {
			
			@Override
			public void run() {
				networkThread.releaseAndStop();
			}
		});

        networkThread.start();

        return future;
    }
}
