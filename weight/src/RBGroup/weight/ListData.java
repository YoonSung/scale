package RBGroup.weight;

public class ListData {
	String id;
	boolean isMan;
	float weight;
	String language;
	
	public ListData(String id, boolean isMan, float weight, String language) {
		this.id = id;
		this.isMan = isMan;
		this.weight = weight;
		this.language = language;
	}

	public String getImgURL() {
		return Common.ROOT_PATH + "/images/" + this.id;
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


