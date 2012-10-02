package net.neonlotus.ucritic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
// http://www.javacodegeeks.com/2011/01/android-json-parsing-gson-tutorial.html
// http://developer.rottentomatoes.com/docs/read/json/v10/Movie_Info
// http://jsonviewer.stack.hu/#http://api.rottentomatoes.com/api/public/v1.0/movies/770672122.json?apikey=vg2cj5tgqmbkkxz2vgyxqyh9
// Paginate the results
// Async Task

public class MyActivity extends Activity implements View.OnClickListener{
    // Constants
    private static final String ROTTEN_TOMATOES_API_KEY = "vg2cj5tgqmbkkxz2vgyxqyh9";

    // Views
	private TextView t1;
	private EditText e1;
	private Button b1, b2;

	private ArrayList<String> mIdList = new ArrayList<String>();
	private ArrayList<String> mTitleList = new ArrayList<String>();

	public String movieID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



		final ListView lv = (ListView)findViewById(android.R.id.list);
		//lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, mIdList));
		lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, mTitleList));

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				String movieTitleFromEditText = lv.getItemAtPosition(position).toString().replace(" ","+");
				String movieQueryUrl = generateMovieQueryUrl(movieTitleFromEditText);

				//String value = lv.getItemAtPosition(position).toString();

				//setMovieID(value); //hmm

				InputStream source = retrieveStream(movieQueryUrl);

				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Query mQuery = gson.fromJson(reader, Query.class);

				List<Movie> mMovie = mQuery.movies;
				MovieObject mObj = getMovieDataWithId(mIdList.get(position));
				String cs = String.valueOf(mObj.ratings.criticsScore);
				Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT).show();
				//Toast.makeText(getApplicationContext(), String.valueOf(mMovie.size()), Toast.LENGTH_SHORT).show();

				//MovieObject mObject = gson.fromJson(reader, MovieObject.class);
				//Toast.makeText(getBaseContext(), mObject.title, Toast.LENGTH_SHORT).show();
			}
		});

		/*lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {

				String value = lv.getItemAtPosition(position).toString();

				setMovieID(value); //hmm

				InputStream source = retrieveStream(generateMovieUrl(movieID));


				MovieObject mObject = getMovieDataWithId(movieID); //gson.fromJson(reader, MovieObject.class);
				String cs = String.valueOf(mObject.ratings.criticsScore);
				Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT).show();
				return false;
			}
		});*/


		t1 = (TextView) findViewById(R.id.text1);
		e1 = (EditText) findViewById(R.id.editOne);
		b1 = (Button) findViewById(R.id.button);
		//b2 = (Button) findViewById(R.id.button2);

		b1.setOnClickListener(this);
		//b2.setOnClickListener(this);

	}

    private MovieObject getMovieDataWithId(String movieID) { //return movie object given an ID
		InputStream source = retrieveStream(generateMovieUrl(movieID));
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(source);
        MovieObject mObject = gson.fromJson(reader, MovieObject.class);

		return mObject;
    }

	public void setMovieID(String idFroMQuery) {
		movieID = idFroMQuery;
	}
	public String getMovieID() {
		return movieID;
	}


	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.button:
				String movieTitleFromEditText = e1.getText().toString().replace(" ","+");
				String movieQueryUrl = generateMovieQueryUrl(movieTitleFromEditText);

				Log.d("ucritic", "request url: " + movieQueryUrl);
				InputStream source = retrieveStream(movieQueryUrl);
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Query movieQuery = gson.fromJson(reader, Query.class);

				List<Movie> movieList = movieQuery.movies;

                /* list have a clear method which can optimize this kind of loop
				for (int i = 0; i < mIdList.size();i++) { //Reset list before populating with new results
					mIdList.remove(i);
				}
				*/
                mIdList.clear();
                mTitleList.clear();

				for (Movie movie : movieList) {
					mIdList.add(movie.id);
				}

                /* I think the title is actually in the search response so this query is probably not needed */
				for (int i = 0;i< mIdList.size();i++) {
					//testing
					String temp_ID = mIdList.get(i);
					InputStream source2 = retrieveStream(generateMovieUrl(temp_ID));

					Gson gson2 = new Gson();

					Reader reader2 = new InputStreamReader(source2);

					MovieObject mObject = gson2.fromJson(reader2, MovieObject.class);
					//Toast.makeText(getBaseContext(), mObject.title, Toast.LENGTH_SHORT).show();
					mTitleList.add(mObject.title);
				}

				break;

//			case R.id.button2:
//				t1.setText("Button 2 test");
//				getMovieDataWithId(t1.getText().toString());
//				break;
		}
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

    /**
     * Generates a movie search(query) url based on input movie title (query)
     * @param movieTitle - movie title
     * @return
     */
    private String generateMovieQueryUrl(String movieTitle) {
        URI uri = null;
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("apikey", ROTTEN_TOMATOES_API_KEY));
        qparams.add(new BasicNameValuePair("q", movieTitle));
        qparams.add(new BasicNameValuePair("page_limit", "10"));
        try {
            uri = URIUtils.createURI("http", "api.rottentomatoes.com", -1, "/api/public/v1.0/movies.json", URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return (uri != null) ? uri.toString() : null;
    }


    /**
     * Generates a movie search(query) url based on input movie id
     * @param movieID - movie title
     * @return
     * //TODO: make these two functions one function
     */
    private String generateMovieUrl(String movieID) {
        //private String url = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+movieID+".json?apikey="+ROTTEN_TOMATOES_API_KEY;
		URI uri = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("apikey", ROTTEN_TOMATOES_API_KEY));


        StringBuilder movieDataPathBuilder = new StringBuilder();
        movieDataPathBuilder.append("/api/public/v1.0/movies/");
        try {
            movieDataPathBuilder.append(URLEncoder.encode(movieID, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        movieDataPathBuilder.append(".json");


        try {
            uri = URIUtils.createURI("http", "api.rottentomatoes.com", -1, movieDataPathBuilder.toString(), URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return (uri != null) ? uri.toString() : null;
    }


    /**
     * Pseudo code example of combining these two functions
     */
    private String generateUrls(String path, List<NameValuePair> params){
        URI uri = null;
        try {
            uri = URIUtils.createURI("http", "api.rottentomatoes.com", -1, path, URLEncodedUtils.format(params, "UTF-8"), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return (uri != null) ? uri.toString() : null;
    }
}