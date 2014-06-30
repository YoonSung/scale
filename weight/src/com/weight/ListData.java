package com.weight;

import java.io.File;

public class ListData {
	private String id;
	private boolean isMan;
	private float weight;
	private String language;
	private String imageUrl;

	public ListData(String id, boolean isMan, float weight, String language) {
		this.id = id;
		this.isMan = isMan;
		this.weight = weight;
		this.language = language;
		this.imageUrl = Common.ROOT_PATH+File.separator+"image?id="+id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMan() {
		return isMan;
	}

	public void setMan(boolean isMan) {
		this.isMan = isMan;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}


