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
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PerformMovieSearch extends AsyncTask<String, Void, ArrayList<Movie>> {
	//class PerformMovieSearch AsyncTask<String, Void, ArrayList<Movie>>() {
	//class PerformMovieSearch AsyncTask<String, Void, String> {


	private final Context context;
	private ProgressDialog progressDialog;


	public PerformMovieSearch(Context context){
		this.context = context;
	}

	@Override
	protected ArrayList<Movie> doInBackground(String... params) {

		try {
			String moviesJsonString = retrieveStream(params[0]);
			JSONObject moviesJson = new JSONObject(moviesJsonString);
		} catch (JSONException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		ArrayList<Movie> movies = new ArrayList<Movie>();
		/*
						 * Do your code to process the JSON and create an ArrayList of films.
						 * It's just a suggestion how to store the data.
						 */
		return movies;
	}

	/*@Override
	protected String doInBackground(String... urls) {
		retrieveStream(urls[0]);
		//return urls[0];
		return null;
		*//*String response = "";
		for (String url : urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s;
				while ((s = buffer.readLine()) != null) {
					response += s;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;*//*
	}*/

	@Override
	protected void onPreExecute() {
		progressDialog= ProgressDialog.show(context, "Please Wait","Searching movies", true);

	}


	/*@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		MyActivity.mListAdapter.notifyDataSetChanged();
	}*/
	protected void onPostExecute(ArrayList<Movie> result) {
		progressDialog.dismiss();
		//create a method to set an ArrayList in your adapter and set it here.
		MyActivity.mListAdapter.setMovies(result);
		MyActivity.mListAdapter.notifyDataSetChanged();
	}


	public String retrieveStream(String url) {

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
			String jsonString = EntityUtils.toString(getResponseEntity);
			return jsonString;
		} catch (IOException e) {
			getRequest.abort();
			Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}



}



/*
	   copy of Inputstream

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