package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import textalytics.entity.CrawlHistory;
import textalytics.entity.Link;

public class CrawlerDAO {
	private pgDAO dao;
	private final String TABLENAME = "crawler",
			LINK = "link",
			CHECKSUM = "checksum";
	
	public CrawlerDAO(pgDAO pd){
		dao =pd;
	}
	
	public List<CrawlHistory> selectAll(){
		HashMap<String,Object> rsMap = dao.selectAll(TABLENAME);
		ResultSet rs = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		List<CrawlHistory> crawls = new ArrayList<CrawlHistory>();
		try{
			while(rs.next()){
				String link = rs.getString(LINK);
				String checksum = rs.getString(CHECKSUM);
				crawls.add(new CrawlHistory(link,checksum));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(conn!=null){
					conn.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return crawls;
	}
	
	public void insertCrawlHistory(CrawlHistory crawl){
		HashMap<String,String> crawlLinks = new HashMap<String,String>();
		crawlLinks.put("name", TABLENAME);
		crawlLinks.put("attributes", "2");
		crawlLinks.put("attribute_1_name", LINK);
		crawlLinks.put("attribute_2_name", CHECKSUM);
		
		crawlLinks.put("values", "1");
		
		crawlLinks.put("values_1_child_1_value",crawl.getLink());
		crawlLinks.put("values_1_child_1_type", "string");
		crawlLinks.put("values_1_child_2_value",crawl.getChecksum());
		crawlLinks.put("values_1_child_2_type", "string");
		
		dao.insert(crawlLinks);
	}
	
	public CrawlHistory getCrawlHistory(String link){
		HashMap<String,String> mapLinks = new HashMap<String,String>();
		mapLinks.put("name", TABLENAME);
		mapLinks.put("conditions", "1");
		mapLinks.put("condition_1", LINK+" ilike '"+link+"'");
		
		HashMap<String,Object> rsMap = dao.select(mapLinks);
		ResultSet rsCrawl = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		try{
			if(rsCrawl.next()){
				String checksumRS = rsCrawl.getString(CHECKSUM),
				linkRS = rsCrawl.getString(LINK);
				return new CrawlHistory(linkRS,checksumRS);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			if(rsCrawl!=null){
				try {
					rsCrawl.close();
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
		return null;
	}
	
	public void updateCrawlHistory(CrawlHistory crawl){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLENAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", LINK+" ilike '"+crawl.getLink()+"'");
		
		summaryMap.put("values", "1");
		summaryMap.put("values_1_attr", CHECKSUM);
		summaryMap.put("values_1", crawl.getChecksum());
		summaryMap.put("values_1_type", "string");
		
		dao.update(summaryMap);
	}
	
	public void truncateTable(){
		dao.truncateTable(TABLENAME);
	}
}
