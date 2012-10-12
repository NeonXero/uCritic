package net.neonlotus.ucritic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

	public void setMovies(ArrayList<Movie> movieList) throws Exception {
		String movieTitleFromEditText = "Toy Story";
		String movieQueryUrl = MyActivity.generateMovieQueryUrl(movieTitleFromEditText);

		InputStream source = null;// = retrieveStream(movieQueryUrl);

		String convertedIS = convertStreamToString(source);
		String mqu = retrieveStream(convertedIS);

		Gson gson = new Gson();
		Reader reader = new InputStreamReader(source);
		Query movieQuery = gson.fromJson(reader, Query.class);
		List<Movie> movieListTwo = movieQuery.movies;

		movieList.add(movieListTwo.get(0));
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		is.close();

		return sb.toString();
	}
}