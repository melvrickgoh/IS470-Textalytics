package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import textalytics.entity.Link;

public class LinksDAO {
	private pgDAO dao;
	private final String TABLENAME_PAGES = "pages",
			PAGES_LINK_PARENT = "parent",
			PAGES_LINK = "link",
			PAGES_LINK_TYPE = "type",
			TABLENAME_PAGES_SUMMARY = "pagessummary",
			SUMMARY_LINK = "link",
			SUMMARY_LINK_COUNT = "linkedToCount";
	
	public LinksDAO(pgDAO pd){
		dao =pd;
		initialize();
	}
	
	public List<Link> selectAll(){
		HashMap<String,Object> rsMap = dao.selectAll(TABLENAME_PAGES);
		ResultSet rs = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		
		List<Link> links = new ArrayList<Link>();
		try{
			while(rs.next()){
				String parent = rs.getString(PAGES_LINK_PARENT);
				String link = rs.getString(PAGES_LINK);
				String type = rs.getString(PAGES_LINK_TYPE);
				links.add(new Link(parent,link,type));
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
		return links;
	}
	
	private void insertSummaryLink(String link){
		HashMap<String,String> summaryLinks = new HashMap<String,String>();
		summaryLinks.put("name", TABLENAME_PAGES_SUMMARY);
		summaryLinks.put("attributes", "2");
		summaryLinks.put("attribute_1_name", SUMMARY_LINK);
		summaryLinks.put("attribute_2_name", SUMMARY_LINK_COUNT);
		
		summaryLinks.put("values", "1");
		
		summaryLinks.put("values_1_child_1_value",link);
		summaryLinks.put("values_1_child_1_type", "string");
		summaryLinks.put("values_1_child_2_value","0");
		summaryLinks.put("values_1_child_2_type", "integer");
		
		dao.insert(summaryLinks);
	}
	
	public void insertLinks(List<Link> links){
		HashMap<String,String> mapLinks = new HashMap<String,String>();
		mapLinks.put("name", TABLENAME_PAGES);
		mapLinks.put("attributes", "3");
		mapLinks.put("attribute_1_name", PAGES_LINK_PARENT);
		mapLinks.put("attribute_2_name", PAGES_LINK);
		mapLinks.put("attribute_3_name", PAGES_LINK_TYPE);
		
		mapLinks.put("values", ""+links.size());
		
		for (int i = 0; i<links.size(); i++){
			Link l = links.get(i);
			mapLinks.put("values_"+(i+1)+"_child_"+1+"_value",l.getParent());
			mapLinks.put("values_"+(i+1)+"_child_"+1+"_type", "string");
			mapLinks.put("values_"+(i+1)+"_child_"+2+"_value",l.getLink());
			mapLinks.put("values_"+(i+1)+"_child_"+2+"_type", "string");
			mapLinks.put("values_"+(i+1)+"_child_"+3+"_value",l.getType());
			mapLinks.put("values_"+(i+1)+"_child_"+3+"_type", "string");
			
			//update links summary
			if (checkIfLinkExists(l.getLink())){
				//increment the summary count
				incrementLinkSummaryCount(l.getLink());
			}else{
				insertSummaryLink(l.getLink());//insert new row if doesn't exist
			}
		}
		
		dao.insert(mapLinks);
	}
	
	public List<Link> getLinks(String link){
		List<Link> links = new ArrayList<Link>();
		HashMap<String,String> mapLinks = new HashMap<String,String>();
		mapLinks.put("name", TABLENAME_PAGES);
		mapLinks.put("conditions", "1");
		mapLinks.put("condition_1", PAGES_LINK+" ilike '"+link+"'");
		
		HashMap<String,Object> rsMap = dao.select(mapLinks);
		ResultSet rsLinks = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		try{
			while(rsLinks.next()){
				String parent = rsLinks.getString(PAGES_LINK_PARENT),
				linked = rsLinks.getString(PAGES_LINK),
				type = rsLinks.getString(PAGES_LINK_TYPE);
				links.add(new Link(parent,linked,type));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				if(rsLinks!=null){
					rsLinks.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return links;
	}
	
	public int incrementLinkSummaryCount(String link){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLENAME_PAGES_SUMMARY);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", SUMMARY_LINK+" ilike '"+link+"'");
		
		summaryMap.put("values", "1");
		summaryMap.put("values_1_attr", SUMMARY_LINK_COUNT);
		summaryMap.put("values_1", SUMMARY_LINK_COUNT+" + 1");
		summaryMap.put("values_1_type", "integer");
		
		summaryMap.put("returns","1");
		summaryMap.put("return_1",SUMMARY_LINK_COUNT);
		
		HashMap<String,Object> rsMap = dao.update(summaryMap);
		ResultSet rs = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		try{
			int newTotal = -1;
			while(rs.next()){
				newTotal = rs.getInt(SUMMARY_LINK_COUNT);
			}
			return newTotal;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
					rs.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	private boolean checkIfLinkExists(String link){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLENAME_PAGES_SUMMARY);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", SUMMARY_LINK+" ilike '"+link+"'");
		HashMap<String,Object> rsMap = dao.select(summaryMap);
		ResultSet rsSummary = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		boolean linkExists = false;
		try{
			while (rsSummary.next()){
				linkExists = true;
				break;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			if(rsSummary!=null){
				try {
					rsSummary.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return linkExists;
	}
	
	public void getLink(String link){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLENAME_PAGES_SUMMARY);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", SUMMARY_LINK+" ilike '"+link+"'");
		
		HashMap<String,Object> rsSumMap = dao.select(summaryMap);
		ResultSet rsSummary = (ResultSet)rsSumMap.get("ResultSet");
		Connection sumConn = (Connection)rsSumMap.get("Connection");
		
		HashMap<String,String> linkMap = new HashMap<String,String>();
		linkMap.put("name", TABLENAME_PAGES);
		linkMap.put("conditions", "1");
		linkMap.put("condition_1", PAGES_LINK_PARENT+" ilike '"+link+"'");
		
		HashMap<String,Object> rsLinkMap = dao.select(linkMap);
		ResultSet rsLinks = (ResultSet)rsLinkMap.get("ResultSet");
		Connection linkConn = (Connection)rsLinkMap.get("Connection");
		
		System.out.println(rsSummary);
		System.out.println(rsLinks);
		
		
		try {
			if (rsSummary!=null){
				rsSummary.close();
			}
			if (sumConn !=null){
				sumConn.close();
			}
			if (rsLinks!=null){
				rsLinks.close();
			}
			if (linkConn!=null){
				linkConn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initialize(){
		bootstrap();
	}
	
	private void bootstrap(){
		createLinksTable();
		createLinksSummaryTable();//for handling how often this page is linked to
	}
	
	private void createLinksSummaryTable(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("name", TABLENAME_PAGES_SUMMARY);
		map.put("pk","id");
		map.put("pk_generated", "true");
		map.put("attributes", "2");
		
		//link
		map.put("attribute_1_name",SUMMARY_LINK);
		map.put("attribute_1_type","varchar(500)" );
		map.put("attribute_1_compulsory","true" );
		
		//count
		map.put("attribute_2_name",SUMMARY_LINK_COUNT);
		map.put("attribute_2_type","integer" );
		map.put("attribute_2_compulsory","true" );
	
		dao.createTable(map);
	}
	
	private void createLinksTable(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("name",TABLENAME_PAGES);
		map.put("pk","id");
		map.put("pk_generated", "true");
		map.put("attributes", "3");
		
		//parent
		map.put("attribute_1_name",PAGES_LINK_PARENT);
		map.put("attribute_1_type","varchar(500)" );
		map.put("attribute_1_compulsory","false" );
		
		//current
		map.put("attribute_2_name",PAGES_LINK);
		map.put("attribute_2_type","varchar(500)" );
		map.put("attribute_2_compulsory","true" );
		
		//type
		map.put("attribute_3_name",PAGES_LINK_TYPE);
		map.put("attribute_3_type","varchar(50)" );
		map.put("attribute_3_compulsory","false" );
	
		dao.createTable(map);
	}
	
	public void truncateTable(){
		dao.truncateTable(TABLENAME_PAGES);
		dao.truncateTable(TABLENAME_PAGES_SUMMARY);
	}
}
