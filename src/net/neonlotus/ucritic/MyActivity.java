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

public class MyActivity extends Activity {
    // Constants
    private static final String ROTTEN_TOMATOES_API_KEY = "vg2cj5tgqmbkkxz2vgyxqyh9";

    // Views
	private TextView t1;
	private EditText e1;
	private Button b1;

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
                String movieTitleFromEditText = e1.getText().toString().replace(" ","+");
                String movieQueryUrl = generateMovieQueryUrl(movieTitleFromEditText);

				//Toast.makeText(MyActivity.this, movieTitle,Toast.LENGTH_SHORT).show();
                Log.d("ucritic", "request url: " + movieQueryUrl);
				InputStream source = retrieveStream(movieQueryUrl);
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Query mQuery = gson.fromJson(reader, Query.class);

                List<Movie> mMovie = mQuery.movies;

				for (Movie movie : mMovie) { //Could not find dictionary pack???
					Toast.makeText(MyActivity.this, movie.id, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

    private void getMovieDataWithId(String movieID) {
        InputStream source = retrieveStream(generateMovieUrl(movieID));

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

    /**
     * Generates a movie search(query) url based on input movie title (query)
     *
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
     *
     * @param movieID - movie title
     * @return
     *
     * //TODO: make these two functions one function
     */
    private String generateMovieUrl(String movieID) {
        //private String url = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+movieID+".json?apikey="+ROTTEN_TOMATOES_API_KEY;

        StringBuilder movieDataPathBuilder = new StringBuilder();
        movieDataPathBuilder.append("/api/public/v1.0/movies/");
        try {
            movieDataPathBuilder.append(URLEncoder.encode(movieID, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        movieDataPathBuilder.append(".json");

        URI uri = null;
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("apikey", ROTTEN_TOMATOES_API_KEY));

        try {
            uri = URIUtils.createURI("http", "api.rottentomatoes.com", -1, movieDataPathBuilder.toString(), URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return (uri != null) ? uri.toString() : null;
    }


    /**
     * Pseudo code example of combining these two functions
     *
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