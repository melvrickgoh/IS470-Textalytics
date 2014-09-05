package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import textalytics.entity.Supervisor;

public class SupervisorDAO {
	private pgDAO dao;
	private final String TABLENAME = "supervisorteams",
			SUPERVISOR = "supervisor",
			TEAM = "team",
			YEAR = "year",
			SEMESTER = "semester";
	
	public SupervisorDAO(pgDAO pd){
		dao =pd;
	}
	
	public void insertSupervisorTeamRecord(String supervisor, String team, int year, int semester){
		HashMap<String,String> recordSummary = new HashMap<String,String>();
		recordSummary.put("name", TABLENAME);
		recordSummary.put("attributes", "4");
		recordSummary.put("attribute_1_name", SUPERVISOR);
		recordSummary.put("attribute_2_name", TEAM);
		recordSummary.put("attribute_3_name", YEAR);
		recordSummary.put("attribute_4_name", SEMESTER);
		
		recordSummary.put("values", "1");

		recordSummary.put("values_1_child_1_value",supervisor);
		recordSummary.put("values_1_child_1_type", "string");
		recordSummary.put("values_1_child_2_value",team);
		recordSummary.put("values_1_child_2_type", "string");
		recordSummary.put("values_1_child_3_value",""+year);
		recordSummary.put("values_1_child_3_type", "integer");
		recordSummary.put("values_1_child_4_value",""+semester);
		recordSummary.put("values_1_child_4_type", "integer");
		
		dao.insert(recordSummary);
	}
	
	public ArrayList<String[]> getSupervisorRecords(String supervisorName){
		ArrayList<String[]> resultList = new ArrayList<String[]>();
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLENAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", SUPERVISOR+"="+supervisorName);
		
		HashMap<String,Object> rsMap = dao.select(summaryMap);
		ResultSet rsSummary = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		
		try{
			while(rsSummary.next()){
				String supervisor = rsSummary.getString(SUPERVISOR),
				team = rsSummary.getString(TEAM);
				int year = rsSummary.getInt(YEAR),
				semester = rsSummary.getInt(SEMESTER);
				resultList.add(new String[]{supervisor,team,""+year,""+semester});
			}
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
		return resultList;
	}
	
	public void truncateTable(){
		dao.truncateTable(TABLENAME);
	}
	
	public List<Supervisor> selectAll(){
		HashMap<String,Object> rsMap = dao.selectAll(TABLENAME);
		ResultSet rs = (ResultSet) rsMap.get("ResultSet");
		Connection conn = (Connection) rsMap.get("Connection");
		List<Supervisor> supervisors = new ArrayList<Supervisor>();
		
		try{
			while(rs.next()){
				String name = rs.getString(SUPERVISOR),
				project = rs.getString(TEAM);
				int year = rs.getInt(YEAR),
				semester = rs.getInt(SEMESTER);
				supervisors.add(new Supervisor(name,project,year,semester));
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
		return supervisors;
	}
}
