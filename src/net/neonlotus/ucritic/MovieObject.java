package net.neonlotus.ucritic;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieObject {

	public List<Rating> ratings; //You are a problem

	@SerializedName("id")
	public String id;

	@SerializedName("title")
	public String title;

	@SerializedName("year")
	public int year;

	@SerializedName("mpaa_rating")
	public String mpaaRating;

	@SerializedName("runtime")
	public int runtime;

	@SerializedName("critics_consensus")
	public String criticsConsensu;

	@SerializedName("synopsis")
	public String synopsis;

	@SerializedName("studio")
	public String studio;
}