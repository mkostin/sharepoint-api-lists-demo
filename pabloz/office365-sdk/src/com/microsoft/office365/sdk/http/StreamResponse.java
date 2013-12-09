package com.microsoft.office365.sdk.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.office365.sdk.Constants;


/**
 * Response implementation based on an InputStream
 */
public class StreamResponse implements Response {
	private BufferedReader mReader;
	private int mStatus;
	Map<String, List<String>> mHeaders;

	/**
	 * Initializes the StreamResponse
	 * @param stream stream to read
	 * @param status HTTP status code
	 */
	public StreamResponse(InputStream stream, int status, Map<String, List<String>> headers) {
		mHeaders = new HashMap<String, List<String>>(headers);
		mReader = new BufferedReader(new InputStreamReader(stream, Constants.UTF8));
		mStatus = status;
	}

	@Override
	public String readToEnd() throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = mReader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public int getStatus() {
		return mStatus;
	}

	@Override
	public String readLine() throws IOException {
		return mReader.readLine();
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		return new HashMap<String, List<String>>(mHeaders);
	}

	@Override
	public List<String> getHeader(String headerName) {
		return mHeaders.get(headerName);
	}
}
