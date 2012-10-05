package net.neonlotus.ucritic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 10/4/12
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerformMovieSearch extends AsyncTask<String, Void, String> {

	private Context context;

	public PerformMovieSearch(Context context){
		this.context = context;
	}

	private final ProgressDialog dialog = new ProgressDialog(context);

	@Override
	protected void onPreExecute() {
		this.dialog.setMessage("Finding movies...");
		this.dialog.show();
	}




	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		for (String url : urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					response += s;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


	public InputStream retrieveStream(String url) {

		DefaultHttpClient client = new DefaultHttpClient();

		HttpGet getRequest = new HttpGet(url);

		try {

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(),
						"Error " + statusCode + " for URL " + url);
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		}
		catch (IOException e) {
			getRequest.abort();
			Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}


	@Override
	protected void onPostExecute(String result) {
		if(this.dialog.isShowing())
			this.dialog.dismiss();

		MyActivity.mListAdapter.notifyDataSetChanged();
	}
}



/*
	   direct copy of InputStream from above

	   private InputStream retrieveStream(String url) {

		   DefaultHttpClient client = new DefaultHttpClient();

		   HttpGet getRequest = new HttpGet(url);

		   try {

			   HttpResponse getResponse = client.execute(getRequest);
			   final int statusCode = getResponse.getStatusLine().getStatusCode();

			   if (statusCode != HttpStatus.SC_OK) {
				   Log.w(getClass().getSimpleName(),
						   "Error " + statusCode + " for URL " + url);
				   return null;
			   }

			   HttpEntity getResponseEntity = getResponse.getEntity();
			   return getResponseEntity.getContent();
		   }
		   catch (IOException e) {
			   getRequest.abort();
			   Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		   }

		   return null;
	   }*/