package textalytics.entity;

public class CrawlHistory {
	private String link, checksum;

	public CrawlHistory(String linkRS, String checksumRS) {
		// TODO Auto-generated constructor stub
		this.link = linkRS;
		this.checksum = checksumRS;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	
}
