package net.neonlotus.ucritic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
	private EditText et_TitleSearch;
	private Button b_TitleSearch;

    private ListView mListView;
    public static ArrayAdapter<String> mListAdapter; //used to be private, had to public so it could be accessed in PMS.java

	private ArrayList<String> mIdList = new ArrayList<String>();
	private ArrayList<String> mTitleList = new ArrayList<String>();

	public String movieID;

	//Async
	PerformMovieSearch performSearch = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mListView = (ListView)findViewById(android.R.id.list);
        mListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, mTitleList);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				String movieTitleFromEditText = mListView.getItemAtPosition(position).toString().replace(" ","+");
				String movieQueryUrl = generateMovieQueryUrl(movieTitleFromEditText);

				//InputStream source = retrieveStream(movieQueryUrl);
				InputStream source = performSearch.retrieveStream(movieQueryUrl);

				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Query mQuery = gson.fromJson(reader, Query.class);

				List<Movie> mMovie = mQuery.movies;
				MovieObject mObj = getMovieDataWithId(mIdList.get(position));
				String cs = String.valueOf(mObj.ratings.criticsScore);
				if (mObj.ratings.criticsScore!=-1) {
					Toast.makeText(getApplicationContext(), "Critic score: " + cs, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "No critic score available.", Toast.LENGTH_SHORT).show();
				}

			}
		});

		et_TitleSearch = (EditText) findViewById(R.id.editOne);
		b_TitleSearch = (Button) findViewById(R.id.button);

		b_TitleSearch.setOnClickListener(this);
        et_TitleSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					search();
					return true;
				}
				return false;
			}
		});

	}

    private MovieObject getMovieDataWithId(String movieID) { //return movie object given an ID
		InputStream source = performSearch.retrieveStream(generateMovieUrl(movieID));
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

    /**
     * This is called when the user clicks the search button or presses enter in the search box
     */
    private void search() {
        String movieTitleFromEditText = et_TitleSearch.getText().toString().replace(" ","+");
        String movieQueryUrl = generateMovieQueryUrl(movieTitleFromEditText);

        Log.d("ucritic", "request url: " + movieQueryUrl);
		InputStream source = performSearch.retrieveStream(movieQueryUrl);
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(source);
        Query movieQuery = gson.fromJson(reader, Query.class);

        List<Movie> movieList = movieQuery.movies;

        mIdList.clear();
        mTitleList.clear();

        for (Movie movie : movieList) {
            mIdList.add(movie.id);
        }

        /* I think the title is actually in the search response so this query is probably not needed */
        for (int i = 0;i< mIdList.size();i++) {
            //testing
            String temp_ID = mIdList.get(i);
            InputStream source2 = performSearch.retrieveStream(generateMovieUrl(temp_ID));

            Gson gson2 = new Gson();

            Reader reader2 = new InputStreamReader(source2);

            MovieObject mObject = gson2.fromJson(reader2, MovieObject.class);
            //Toast.makeText(getBaseContext(), mObject.title, Toast.LENGTH_SHORT).show();
            mTitleList.add(mObject.title);
        }

        // Now that we have new data we need to refresh the list like so
        mListAdapter.notifyDataSetChanged();
    }

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.button:
                search();
				break;
		}
	}


	/*
	moving to async task class
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
        qparams.add(new BasicNameValuePair("page_limit", "50"));
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