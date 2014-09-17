package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import textalytics.entity.Keyword;
import textalytics.entity.StemRecord;

public class StemsDAO {
	private pgDAO dao;
	private final String TABLE_NAME = "stems",
			WORD = "stem",
			TYPE = "type",
			TEAMID = "teamid",
			ID = "id",
			FREQ = "frequency",
			TF = "tf",
			TERMS = "terms";
	
	public StemsDAO(pgDAO pd){
		dao = pd;
	}
	public List<StemRecord> selectAll(){
		List<StemRecord> stems = new ArrayList<StemRecord>();
		HashMap<String,Object> rsMap = dao.selectAll(TABLE_NAME);
		ResultSet rs = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		
		try{
			while(rs.next()){
				int id = rs.getInt(ID);
				String word = rs.getString(WORD);
				String type = rs.getString(TYPE);
				int teamID = rs.getInt(TEAMID);
				int frequency = rs.getInt(FREQ);
				double tf = rs.getDouble(TF);
				
				//dealing with string terms
				String termStr = rs.getString(TERMS);
				HashSet<String> terms = new HashSet<String>();
				terms.addAll(Arrays.asList(termStr.split(",")));
								
				stems.add(new StemRecord(id,teamID,word,type,new Keyword(word,terms,frequency,tf)));
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
		return stems;
	}
	
	public void updateStemFrequencies(List<StemRecord> stems){
		for (int i = 0; i<stems.size(); i++){
			HashMap<String,String> stemLinks = new HashMap<String,String>();
			stemLinks.put("name", TABLE_NAME);
			
			stemLinks.put("attributes", "3");
			stemLinks.put("attribute_1_name", FREQ);
			stemLinks.put("attribute_2_name", TF);
			stemLinks.put("attribute_3_name", TERMS);
			
			StemRecord s = stems.get(i);
			stemLinks.put("conditions", "1");
			stemLinks.put("condition_1", ID+"="+s.getId());
			
			stemLinks.put("values", "3");
			
			Keyword kw = s.getKeyword();
			stemLinks.put("values_1_attr", FREQ);
			stemLinks.put("values_1", ""+kw.getFrequency());
			stemLinks.put("values_1_type", "integer");
			stemLinks.put("values_2_attr", TF);
			stemLinks.put("values_2",""+kw.getTF());
			stemLinks.put("values_2_type", "double");
			stemLinks.put("values_2_attr", TERMS);
			stemLinks.put("values_2", kw.getTermsDBString());
			stemLinks.put("values_2_type", "string");
			
			dao.update(stemLinks);
		}
	}
	
	public void updateStemRecords(List<StemRecord> stems){
		for (int i = 0; i<stems.size(); i++){
			HashMap<String,String> stemLinks = new HashMap<String,String>();
			stemLinks.put("name", TABLE_NAME);
			
			stemLinks.put("attributes", "2");
			stemLinks.put("attribute_1_name", WORD);
			stemLinks.put("attribute_2_name", TYPE);
			
			StemRecord s = stems.get(i);
			stemLinks.put("conditions", "1");
			stemLinks.put("condition_1", ID+"="+s.getId());
			
			stemLinks.put("values", "2");
			
			stemLinks.put("values_1_attr", WORD);
			stemLinks.put("values_1", s.getWord());
			stemLinks.put("values_1_type", "string");
			stemLinks.put("values_2_attr", TYPE);
			stemLinks.put("values_2", s.getType());
			stemLinks.put("values_2_type", "string");
			
			dao.update(stemLinks);
		}
	}
	
	public void insertStemRecords(List<StemRecord> stems){
		HashMap<String,String> stemLinks = new HashMap<String,String>();
		stemLinks.put("name", TABLE_NAME);
		stemLinks.put("attributes", "6");
		stemLinks.put("attribute_1_name", WORD);
		stemLinks.put("attribute_2_name", TYPE);
		stemLinks.put("attribute_3_name", TEAMID);
		stemLinks.put("attribute_4_name", FREQ);
		stemLinks.put("attribute_5_name", TF);
		stemLinks.put("attribute_6_name", TERMS);
		
		stemLinks.put("values", ""+stems.size());
		
		for (int i = 0; i<stems.size(); i++){
			StemRecord s = stems.get(i);
			stemLinks.put("values_"+(i+1)+"_child_"+1+"_value",s.getWord());
			stemLinks.put("values_"+(i+1)+"_child_"+1+"_type", "string");
			stemLinks.put("values_"+(i+1)+"_child_"+2+"_value",s.getType());
			stemLinks.put("values_"+(i+1)+"_child_"+2+"_type", "string");
			stemLinks.put("values_"+(i+1)+"_child_"+3+"_value",""+s.getTeamID());
			stemLinks.put("values_"+(i+1)+"_child_"+3+"_type", "integer");
			
			Keyword kw = s.getKeyword();
			stemLinks.put("values_"+(i+1)+"_child_"+4+"_value",""+kw.getFrequency());
			stemLinks.put("values_"+(i+1)+"_child_"+4+"_type", "integer");
			stemLinks.put("values_"+(i+1)+"_child_"+5+"_value",""+kw.getTF());
			stemLinks.put("values_"+(i+1)+"_child_"+5+"_type", "double");
			stemLinks.put("values_"+(i+1)+"_child_"+6+"_value",kw.getTermsDBString());
			stemLinks.put("values_"+(i+1)+"_child_"+6+"_type", "string");
		}
		
		dao.insert(stemLinks);
	}
	
	public StemRecord getStemRecordID(int id){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLE_NAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", ID+"="+id);
		
		
		HashMap<String,Object> rsMap = dao.select(summaryMap);
		ResultSet rsStem = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		try {
			while(rsStem.next()){
				int stemID = rsStem.getInt(ID);
				String word = rsStem.getString(WORD);
				String type = rsStem.getString(TYPE);
				int teamID = rsStem.getInt(TEAMID);
				int frequency = rsStem.getInt(FREQ);
				double tf = rsStem.getDouble(TF);
				
				//dealing with string terms
				String termStr = rsStem.getString(TERMS);
				HashSet<String> terms = new HashSet<String>();
				terms.addAll(Arrays.asList(termStr.split(",")));
				
				return new StemRecord(stemID,teamID,word,type,new Keyword(word,terms,frequency,tf));
			}
			
			if (rsStem!=null){
				rsStem.close();
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
	
	public ArrayList<StemRecord> getTeamSummaryStems(int teamID){
		 return getConditionalStems("team",teamID);
	}
	
	public ArrayList<StemRecord> getWordStems(String word){
		 return getConditionalStems("word",word);
	}
	
	public ArrayList<StemRecord> getConditionalStems(String conditionType, Object value){
		ArrayList<StemRecord> stems = new ArrayList<StemRecord>();
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		
		summaryMap.put("name", TABLE_NAME);
		summaryMap.put("conditions", "1");
		switch(conditionType){
			case "word":
				String word = (String) value;
				summaryMap.put("condition_1", WORD+"="+word);
				break;
			case "team":
				int teamID = (Integer) value;
				summaryMap.put("condition_1", TEAMID+"="+teamID);
				break;
			default:
				String word2 = (String) value;
				summaryMap.put("condition_1", WORD+"="+word2);
		}
		
		
		HashMap<String,Object> rsMap = dao.select(summaryMap);
		ResultSet rsStem = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		try {
			while(rsStem.next()){
				int id = rsStem.getInt(ID);
				String stemWord = rsStem.getString(WORD);
				String type = rsStem.getString(TYPE);
				int teamID = rsStem.getInt(TEAMID);
				int frequency = rsStem.getInt(FREQ);
				double tf = rsStem.getDouble(TF);
				
				//dealing with string terms
				String termStr = rsStem.getString(TERMS);
				HashSet<String> terms = new HashSet<String>();
				terms.addAll(Arrays.asList(termStr.split(",")));
				
				stems.add(new StemRecord(id,teamID,stemWord,type,new Keyword(stemWord,terms,frequency,tf)));
			}
			
			if (rsStem!=null){
				rsStem.close();
			}
			if (conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stems;
	}
	
	public boolean checkIfStemExists(String word){
		HashMap<String,String> stemMap = new HashMap<String,String>();
		stemMap.put("name", TABLE_NAME);
		stemMap.put("conditions", "1");
		stemMap.put("condition_1", WORD+"="+word);
		
		HashMap<String,Object> rsStem = dao.select(stemMap);
		ResultSet rsSummary = (ResultSet) rsStem.get("ResultSet");
		Connection conn = (Connection) rsStem.get("Connection");
		
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
