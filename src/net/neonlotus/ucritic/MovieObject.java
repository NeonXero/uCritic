package net.neonlotus.ucritic;

import com.google.gson.annotations.SerializedName;

class MovieObject {

	public Rating ratings; //You are a problem

	@SerializedName("id")
	public String id;

	@SerializedName("title")
	public String title;

	@SerializedName("year")
	//public int year;
	public String year;

	@SerializedName("mpaa_rating")
	public String mpaaRating;

	@SerializedName("runtime")
	//public int runtime;
	public String runtime;

	@SerializedName("critics_consensus")
	public String criticsConsensu;

	@SerializedName("synopsis")
	public String synopsis;

	@SerializedName("studio")
	public String studio;
}