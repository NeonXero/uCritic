package net.neonlotus.ucritic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
// http://www.javacodegeeks.com/2011/01/android-json-parsing-gson-tutorial.html
// http://developer.rottentomatoes.com/docs/read/json/v10/Movie_Info
// http://jsonviewer.stack.hu/#http://api.rottentomatoes.com/api/public/v1.0/movies/770672122.json?apikey=vg2cj5tgqmbkkxz2vgyxqyh9

public class MyActivity extends Activity {

	TextView t1;
	EditText e1;
	Button b1;
	// url to make request
	static String myAPI = "vg2cj5tgqmbkkxz2vgyxqyh9";
	//static String movieID = "770672122";
	static String movieID = "";
	static String movieTitle = "";


	private static String movieQuery = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey="+myAPI+"&q="+movieTitle+"&page_limit=10";
	private static String url = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+movieID+".json?apikey="+myAPI;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		t1 = (TextView) findViewById(R.id.text1);
		e1 = (EditText) findViewById(R.id.editOne);
		b1 = (Button) findViewById(R.id.button);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				movieTitle = e1.getText().toString().replace(" ","+");
				String temp2 = "";

				//Toast.makeText(MyActivity.this, movieTitle,Toast.LENGTH_SHORT).show();

				InputStream source = retrieveStream(movieQuery);
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Query mQuery = gson.fromJson(reader, Query.class);
				List<Movie> mMovie = mQuery.movies;

				for (Movie movie : mMovie) { //Could not find dictionary pack???
					Toast.makeText(MyActivity.this, movie.id,Toast.LENGTH_SHORT).show();
				}

				t1.setText(temp2);
			}
		});

		InputStream source = retrieveStream(url);

		Gson gson = new Gson();

		Reader reader = new InputStreamReader(source);

		MovieObject mObject = gson.fromJson(reader, MovieObject.class); //Expected BEGIN_ARRAY but was BEGIN_OBJECT instead. Something to do with the Rating object I believe.
		Rating ratings = mObject.ratings;

		//Toast.makeText(this, mObject.title, Toast.LENGTH_SHORT).show();
		//Toast.makeText(this, mObject.synopsis,Toast.LENGTH_SHORT).show();

		//Toast.makeText(this, String.valueOf(ratings.criticsScore),Toast.LENGTH_SHORT).show();




	}

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
	}





}