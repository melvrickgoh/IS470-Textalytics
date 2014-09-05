package textalytics.entity;

public class Supervisor {
	private String name, team;
	private int year, semester;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public Supervisor(String name, String team, int year, int semester) {
		super();
		this.name = name;
		this.team = team;
		this.year = year;
		this.semester = semester;
	}
	
	
}
