package textalytics.entity;

import java.util.ArrayList;

public class Team {
	private String name, page, pitch, acceptance, poster, midterms, finals, projectreference, description, members, sponsor, descriptionLinks, sponsorLinks;
	private int semester, year;

	public Team(String name, String page, String pitch, String acceptance,
			String poster, String midterms, String finals,
			String projectreference, String description, String members,
			String sponsor, String descriptionLinks, String sponsorLinks, int semester, int year) {
		super();
		this.name = name;
		this.page = page;
		this.pitch = pitch;
		this.acceptance = acceptance;
		this.poster = poster;
		this.midterms = midterms;
		this.finals = finals;
		this.projectreference = projectreference;
		this.description = description;
		this.members = members;
		this.sponsor = sponsor;
		this.descriptionLinks = descriptionLinks;
		this.sponsorLinks = sponsorLinks;
		this.semester = semester;
		this.year = year;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getPitch() {
		return pitch;
	}
	public void setPitch(String pitch) {
		this.pitch = pitch;
	}
	public String getAcceptance() {
		return acceptance;
	}
	public void setAcceptance(String acceptance) {
		this.acceptance = acceptance;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public String getMidterms() {
		return midterms;
	}
	public void setMidterms(String midterms) {
		this.midterms = midterms;
	}
	public String getFinals() {
		return finals;
	}
	public void setFinals(String finals) {
		this.finals = finals;
	}
	public String getProjectreference() {
		return projectreference;
	}
	public void setProjectreference(String projectreference) {
		this.projectreference = projectreference;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMembers() {
		return members;
	}
	public void setMembers(String members) {
		this.members = members;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getSponsorLinks() {
		return sponsorLinks;
	}
	public void setSponsorLinks(String sponsorLinks) {
		this.sponsorLinks = sponsorLinks;
	}
	public String getDescriptionLinks() {
		return descriptionLinks;
	}
	public void setDescriptionLinks(String descriptionLinks) {
		this.descriptionLinks = descriptionLinks;
	}
	
	
}
