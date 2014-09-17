package textalytics.entity;

public class StemRecord {
	private int id, teamID;
	private String word, type;
	private Keyword keyword;
	
	public Keyword getKeyword(){
		return keyword;
	}
	public void setKeyword(Keyword kw){
		keyword = kw;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public StemRecord(int id, int teamID, String word, String type,Keyword kw) {
		super();
		this.id = id;
		this.teamID = teamID;
		this.word = word;
		this.type = type;
		this.keyword = kw;
	}
	public StemRecord(int id, int teamID, String word, String type) {
		super();
		this.id = id;
		this.teamID = teamID;
		this.word = word;
		this.type = type;
	}
	public StemRecord(int teamID, String word, String type) {
		super();
		this.teamID = teamID;
		this.word = word;
		this.type = type;
	}
	public StemRecord(int teamID, String word, String type, Keyword keyword) {
		super();
		this.teamID = teamID;
		this.word = word;
		this.type = type;
		this.keyword = keyword;
	}
}
