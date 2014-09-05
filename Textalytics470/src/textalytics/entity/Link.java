package textalytics.entity;

public class Link {
	private String parent;
	private String link;
	private String type;
	
	public Link(String parent, String link, String type){
		this.parent = parent;
		this.link = link;
		this.type = type;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
