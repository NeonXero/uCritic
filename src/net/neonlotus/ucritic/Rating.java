package net.neonlotus.ucritic;

import com.google.gson.annotations.SerializedName;

public class Rating {

	@SerializedName("critics_rating")
	public String criticsRating;

	@SerializedName("critics_score")
	public int criticsScore;

	@SerializedName("audience_rating")
	public String audienceRating;

	@SerializedName("audience_score")
	public int audienceScore;


}