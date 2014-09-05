package textalytics.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import textalytics.entity.Student;

public class StudentsDAO {
	private pgDAO dao;
	private final String TABLE_NAME = "students",
			NAME = "name",
			PROJECTS = "projects",
			ROLES = "role";
	
	public StudentsDAO(pgDAO pd){
		dao = pd;
	}
	
	public void updateStudents(List<Student> students){
		for (int i = 0; i<students.size(); i++){
			HashMap<String,String> studentLinks = new HashMap<String,String>();
			studentLinks.put("name", TABLE_NAME);
			
			studentLinks.put("attributes", "2");
			studentLinks.put("attribute_1_name", PROJECTS);
			studentLinks.put("attribute_2_name", ROLES);
			
			Student s = students.get(i);
			studentLinks.put("conditions", "1");
			studentLinks.put("condition_1", NAME+"="+s.getName());
			
			studentLinks.put("values", "2");
			studentLinks.put("values_1_attr", PROJECTS);
			studentLinks.put("values_1", s.getProject());
			studentLinks.put("values_1_type", "string");
			studentLinks.put("values_2_attr", ROLES);
			studentLinks.put("values_2", s.getRoles());
			studentLinks.put("values_2_type", "string");
			
			dao.update(studentLinks);
		}
	}
	
	public void insertStudents(List<Student> students){
		HashMap<String,String> studentLinks = new HashMap<String,String>();
		studentLinks.put("name", TABLE_NAME);
		studentLinks.put("attributes", "3");
		studentLinks.put("attribute_1_name", NAME);
		studentLinks.put("attribute_2_name", PROJECTS);
		studentLinks.put("attribute_3_name", ROLES);
		
		studentLinks.put("values", ""+students.size());
		
		for (int i = 0; i<students.size(); i++){
			Student s = students.get(i);
			studentLinks.put("values_"+(i+1)+"_child_"+1+"_value",s.getName());
			studentLinks.put("values_"+(i+1)+"_child_"+1+"_type", "string");
			studentLinks.put("values_"+(i+1)+"_child_"+2+"_value",s.getProject());
			studentLinks.put("values_"+(i+1)+"_child_"+2+"_type", "string");
			studentLinks.put("values_"+(i+1)+"_child_"+3+"_value",s.getRoles());
			studentLinks.put("values_"+(i+1)+"_child_"+3+"_type", "string");
		}
		
		dao.insert(studentLinks);
	}
	
	//HAVENT IMPLEMENT THE RETRIEVAL OF STUDENT
	public Student getStudent(String name){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLE_NAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", NAME+"="+name);
		
		HashMap<String,Object> map = dao.select(summaryMap);
		ResultSet rsStudent =(ResultSet)map.get("ResultSet");
		Connection conn =(Connection)map.get("Connection");
		
		try {
			while(rsStudent.next()){
				String studentName = rsStudent.getString(NAME);
				String studentProjects = rsStudent.getString(PROJECTS);
				String studentRole = rsStudent.getString(ROLES);
				return new Student(studentName,studentProjects,studentRole);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				if (rsStudent!=null){
					rsStudent.close();
				}
				if (conn!=null){
					conn.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public boolean checkIfStudentExists(String name){
		HashMap<String,String> summaryMap = new HashMap<String,String>();
		summaryMap.put("name", TABLE_NAME);
		summaryMap.put("conditions", "1");
		summaryMap.put("condition_1", NAME+"="+name);
		
		HashMap<String,Object> map = dao.select(summaryMap);
		ResultSet rsSummary = (ResultSet)map.get("ResultSet");
		Connection conn = (Connection)map.get("Connection");
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
		System.out.println("truncating students dao");
		dao.truncateTable(TABLE_NAME);
	}
	
	public List<Student> selectAll(){
		List<Student> students = new ArrayList<Student>();
		HashMap<String,Object> rsMap = dao.selectAll(TABLE_NAME);
		ResultSet rs = (ResultSet)rsMap.get("ResultSet");
		Connection conn = (Connection)rsMap.get("Connection");
		try{
			while(rs.next()){
				String name = rs.getString(NAME);
				String project = rs.getString(PROJECTS);
				String roles = rs.getString(ROLES);
				students.add(new Student(name,project,roles));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return students;
	}
}
