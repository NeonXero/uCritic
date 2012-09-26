package net.neonlotus.ucritic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MyActivity extends Activity implements View.OnClickListener {

	TextView t1;
	// url to make request
	static String myAPI = "vg2cj5tgqmbkkxz2vgyxqyh9";
	static String movieID = "770672122";
	private static String url = ("http://api.rottentomatoes.com/api/public/v1.0/movies/"+movieID+".json?apikey="+myAPI);


	private String jstring;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		t1 = (TextView) findViewById(R.id.text1);

		InputStream source = retrieveStream(url);

		Gson gson = new Gson();
		// SO
		//JsonParser parser = new JsonParser();
		//JsonArray Jarray = parser.parse(jstring).getAsJsonArray();

		//ArrayList<Rating> lcs = new ArrayList<Rating>();

		//for(JsonElement obj : Jarray )
		//{
		//	Rating cse = gson.fromJson( obj , Rating.class);
		//	lcs.add(cse);
		//}
		//Toast.makeText(MyActivity.this,lcs.get(0).toString(),Toast.LENGTH_SHORT).show();

		// SO

		Reader reader = new InputStreamReader(source);

		MovieObject mObject = gson.fromJson(reader, MovieObject.class); //wat

		Toast.makeText(this, mObject.title,Toast.LENGTH_SHORT).show();

		List<Rating> ratings = mObject.ratings;

		//t1.setText(results.get(0).toString());




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

	public void onClick(View view) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}