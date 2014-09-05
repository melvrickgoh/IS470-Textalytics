package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import textalytics.entity.Link;
import textalytics.entity.Team;

public class TeamsDAO {
	private pgDAO dao;
	private final String TABLE_NAME = "teams",
			NAME = "teamname",
			PAGE = "teampage",
			PITCH = "teampitch",
			ACCEPTANCE = "teamacceptance",
			POSTER = "teamposter",
			MIDTERMS = "teammidterm",
			FINALS = "teamfinalpresentation",
			REFERENCE = "projectreference",
			DESCRIPTION = "projectdescription",
			DESCRIPTIONLINKS = "descriptionlinks",
			MEMBERS = "teammembers",
			SPONSOR = "sponsor",
			SPONSORLINK = "sponsorlink",
			SEMESTER = "semester",
			YEAR = "year";
	public TeamsDAO(pgDAO pd){
		dao = pd;
	}
	
	public List<Team> selectAll(){
		List<Team> teams = new ArrayList<Team>();
		HashMap<String,Object> rsMap = dao.selectAll(TABLE_NAME);
		ResultSet rs = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		
		try{
			while(rs.next()){
				String name = rs.getString(NAME);
				String page = rs.getString(PAGE);
				String pitch = rs.getString(PITCH);
				String acceptance = rs.getString(ACCEPTANCE);
				String poster = rs.getString(POSTER);
				String midterms = rs.getString(MIDTERMS);
				String finals = rs.getString(FINALS);
				String projectreference = rs.getString(REFERENCE);
				String description = rs.getString(DESCRIPTION);
				String members = rs.getString(MEMBERS);
				String sponsor = rs.getString(SPONSOR);
				String descriptionLinks = rs.getString(DESCRIPTIONLINKS);
				String sponsorLinks = rs.getString(SPONSORLINK);
				int year = rs.getInt(YEAR);
				int semester = rs.getInt(SEMESTER);
				teams.add(new Team(name,page,pitch,acceptance,poster,midterms,finals,projectreference,description,members,sponsor,descriptionLinks,sponsorLinks,semester,year));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){rs.close();}
				if(conn!=null){conn.close();}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return teams;
	}
	
	public void updateTeams(List<Team> teams){
		for (int i = 0; i<teams.size(); i++){
			HashMap<String,String> teamLinks = new HashMap<String,String>();
			teamLinks.put("name", TABLE_NAME);
			
			teamLinks.put("attributes", "14");
			teamLinks.put("attribute_1_name", PAGE);
			teamLinks.put("attribute_2_name", PITCH);
			teamLinks.put("attribute_3_name", ACCEPTANCE);
			teamLinks.put("attribute_4_name", POSTER);
			teamLinks.put("attribute_5_name", MIDTERMS);
			teamLinks.put("attribute_6_name", FINALS);
			teamLinks.put("attribute_7_name", REFERENCE);
			teamLinks.put("attribute_8_name", DESCRIPTION);
			teamLinks.put("attribute_9_name", MEMBERS);
			teamLinks.put("attribute_10_name", SPONSOR);
			teamLinks.put("attribute_11_name", SEMESTER);
			teamLinks.put("attribute_12_name", YEAR);
			teamLinks.put("attribute_13_name", DESCRIPTIONLINKS);
			teamLinks.put("attribute_14_name", SPONSORLINK);
			
			Team t = teams.get(i);
			teamLinks.put("conditions", "1");
			teamLinks.put("condition_1", NAME+"="+t.getName());
			
			teamLinks.put("values", "2");
			
			teamLinks.put("values_1_attr", PAGE);
			teamLinks.put("values_1", t.getPage());
			teamLinks.put("values_1_type", "string");
			teamLinks.put("values_2_attr", PITCH);
			teamLinks.put("values_2", t.getPitch());
			teamLinks.put("values_2_type", "string");
			teamLinks.put("values_3_attr", ACCEPTANCE);
			teamLinks.put("values_3", t.getAcceptance());
			teamLinks.put("values_3_type", "string");
			teamLinks.put("values_4_attr", POSTER);
			teamLinks.put("values_4", t.getPoster());
			teamLinks.put("values_4_type", "string");
			teamLinks.put("values_5_attr", MIDTERMS);
			teamLinks.put("values_5", t.getMidterms());
			teamLinks.put("values_5_type", "string");
			teamLinks.put("values_6_attr", FINALS);
			teamLinks.put("values_6", t.getFinals());
			teamLinks.put("values_6_type", "string");
			teamLinks.put("values_7_attr", REFERENCE);
			teamLinks.put("values_7", t.getProjectreference());
			teamLinks.put("values_7_type", "string");
			teamLinks.put("values_8_attr", DESCRIPTION);
			teamLinks.put("values_8", t.getDescription());
			teamLinks.put("values_8_type", "string");
			teamLinks.put("values_9_attr", MEMBERS);
			teamLinks.put("values_9", t.getMembers());
			teamLinks.put("values_9_type", "string");
			teamLinks.put("values_10_attr", SPONSOR);
			teamLinks.put("values_10", t.getSponsor());
			teamLinks.put("values_10_type", "string");
			teamLinks.put("values_11_attr", SEMESTER);
			teamLinks.put("values_11", ""+t.getSemester());
			teamLinks.put("values_11_type", "integer");
			teamLinks.put("values_12_attr", YEAR);
			teamLinks.put("values_12", ""+t.getYear());
			teamLinks.put("values_12_type", "integer");
			teamLinks.put("values_13_attr", DESCRIPTIONLINKS);
			teamLinks.put("values_13", ""+t.getDescriptionLinks());
			teamLinks.put("values_13_type", "string");
			teamLinks.put("values_14_attr", SPONSORLINK);
			teamLinks.put("values_14", ""+t.getSponsorLinks());
			teamLinks.put("values_14_type", "string");
			
			dao.update(teamLinks);
		}
	}
	
	public void insertTeams(List<Team> teams){
		HashMap<String,String> teamLinks = new HashMap<String,String>();
		teamLinks.put("name", TABLE_NAME);
		teamLinks.put("attributes", "15");
		teamLinks.put("attribute_1_name", NAME);
		teamLinks.put("attribute_2_name", PAGE);
		teamLinks.put("attribute_3_name", PITCH);
		teamLinks.put("attribute_4_name", ACCEPTANCE);
		teamLinks.put("attribute_5_name", POSTER);
		teamLinks.put("attribute_6_name", MIDTERMS);
		teamLinks.put("attribute_7_name", FINALS);
		teamLinks.put("attribute_8_name", REFERENCE);
		teamLinks.put("attribute_9_name", DESCRIPTION);
		teamLinks.put("attribute_10_name", MEMBERS);
		teamLinks.put("attribute_11_name", SPONSOR);
		teamLinks.put("attribute_12_name", SEMESTER);
		teamLinks.put("attribute_13_name", YEAR);
		teamLinks.put("attribute_14_name", DESCRIPTIONLINKS);
		teamLinks.put("attribute_15_name", SPONSORLINK);
		
		teamLinks.put("values", ""+teams.size());
		
		for (int i = 0; i<teams.size(); i++){
			Team t = teams.get(i);
			teamLinks.put("values_"+(i+1)+"_child_"+1+"_value",t.getName());
			teamLinks.put("values_"+(i+1)+"_child_"+1+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+2+"_value",t.getPage());
			teamLinks.put("values_"+(i+1)+"_child_"+2+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+3+"_value",t.getPitch());
			teamLinks.put("values_"+(i+1)+"_child_"+3+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+4+"_value",t.getAcceptance());
			teamLinks.put("values_"+(i+1)+"_child_"+4+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+5+"_value",t.getPoster());
			teamLinks.put("values_"+(i+1)+"_child_"+5+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+6+"_value",t.getMidterms());
			teamLinks.put("values_"+(i+1)+"_child_"+6+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+7+"_value",t.getFinals());
			teamLinks.put("values_"+(i+1)+"_child_"+7+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+8+"_value",t.getProjectreference());
			teamLinks.put("values_"+(i+1)+"_child_"+8+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+9+"_value",t.getDescription());
			teamLinks.put("values_"+(i+1)+"_child_"+9+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+10+"_value",t.getMembers());
			teamLinks.put("values_"+(i+1)+"_child_"+10+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+11+"_value",t.getSponsor());
			teamLinks.put("values_"+(i+1)+"_child_"+11+"_type", "string");
			teamLinks.put("values_"+(i+1)+"_child_"+12+"_value",""+t.getSemester());
			teamLinks.put("values_"+(i+1)+"_child_"+12+"_type", "integer");
			teamLinks.put("values_"+(i+1)+"_child_"+13+"_value",""+t.getYear());
			teamLinks.put("values_"+(i+1)+"_child_"+13+"_type", "integer");
			teamLinks.put("values_"+(i+1)+"_child_"+14+"_value",""+t.getDescriptionLinks());
			teamLinks.put("values_"+(i+1)+"_child_"+14+"_type", "integer");
			teamLinks.put("values_"+(i+1)+"_child_"+15+"_value",""+t.getSponsorLinks());
			teamLinks.put("values_"+(i+1)+"_child_"+15+"_type", "integer");
		}
		
		dao.insert(teamLinks);
	}
	
	//Tentative Team retrieval
	public Team getTeam(String name){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLE_NAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", NAME+"="+name);
		
		HashMap<String,Object> rsMap = dao.select(summaryMap);
		ResultSet rsTeam = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		try {
			while(rsTeam.next()){
				String teamname = rsTeam.getString(NAME);
				String pageURL = rsTeam.getString(PAGE);
				String pitchURL = rsTeam.getString(PITCH);
				String acceptanceURL = rsTeam.getString(ACCEPTANCE);
				String postersURL = rsTeam.getString(POSTER);
				String midtermsURL = rsTeam.getString(MIDTERMS);
				String finalsURL = rsTeam.getString(FINALS);
				String projectReference = rsTeam.getString(REFERENCE);
				String description = rsTeam.getString(DESCRIPTION);
				String members = rsTeam.getString(MEMBERS);
				String sponsor = rsTeam.getString(SPONSOR);
				int semester = rsTeam.getInt(SEMESTER);
				int year = rsTeam.getInt(YEAR);
				String descriptionLinks = rsTeam.getString(DESCRIPTIONLINKS);
				String sponsorLinks = rsTeam.getString(SPONSORLINK);
				
				return new Team(teamname,pageURL,pitchURL,acceptanceURL,postersURL,midtermsURL,finalsURL,
						projectReference,description,members,sponsor,descriptionLinks,sponsorLinks,semester,year);
			}
			
			if (rsTeam!=null){
				rsTeam.close();
			}
			if (conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean checkIfTeamExists(String name){
		HashMap<String,String> teamMap = new HashMap<String,String>();
		teamMap.put("name", TABLE_NAME);
		teamMap.put("conditions", "1");
		teamMap.put("condition_1", NAME+"="+name);
		
		HashMap<String,Object> rsMap = dao.select(teamMap);
		ResultSet rsSummary = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		try{
			return rsSummary.next();//return if true or false directly
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				if(rsSummary!=null){
					rsSummary.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void truncateTable(){
		dao.truncateTable(TABLE_NAME);
	}
}
