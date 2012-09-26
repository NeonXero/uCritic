package net.neonlotus.ucritic;

import com.google.gson.annotations.SerializedName;

public class Poster {

	@SerializedName("thumbnail")
	public String thumbnail;

	@SerializedName("profile")
	public int profile;

	@SerializedName("detailed")
	public String detailed;

	@SerializedName("original")
	public int original;


}