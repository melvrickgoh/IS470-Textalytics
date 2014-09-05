package textalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import textalytics.dao.CrawlerDAO;
import textalytics.dao.LinksDAO;
import textalytics.dao.StudentsDAO;
import textalytics.dao.SupervisorDAO;
import textalytics.dao.TeamsDAO;
import textalytics.entity.CrawlHistory;
import textalytics.entity.Link;
import textalytics.entity.Student;
import textalytics.entity.Supervisor;
import textalytics.entity.Team;


public class DatabaseServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7052750707015477085L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
				
		ServletContext context = req.getSession().getServletContext();

		String operationType = req.getParameter("operation");
		String jsonString = req.getParameter("json");
		
		JSONObject jsonDetails = (JSONObject) JSONValue.parse(jsonString);
		System.out.println(operationType);
		Object result = null;
		switch (operationType){
			case "truncate":
				result = processTableTruncation(context,jsonDetails);
				break;
			case "select":
				result = processDataSelection(context,jsonDetails);
				break;
			default:
		}
		
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("result", result);
		
		out.println(responseJSON.toJSONString());
	}

	private Object processDataSelection(ServletContext context,JSONObject jsonDetails) {
		// TODO Auto-generated method stub
		String tableName = (String) jsonDetails.get("table");
		
		CrawlerDAO crawlDAO = (CrawlerDAO)context.getAttribute("crawlerDAO");
		TeamsDAO teamsDAO = (TeamsDAO)context.getAttribute("teamsDAO");
		LinksDAO linksDAO = (LinksDAO)context.getAttribute("linksDAO");
		StudentsDAO studentsDAO = (StudentsDAO)context.getAttribute("studentsDAO");
		SupervisorDAO supervisorDAO = (SupervisorDAO)context.getAttribute("supervisorDAO");
		switch(tableName){
			case "crawler":
				List<CrawlHistory> crawlHistories = crawlDAO.selectAll();
				JSONArray dataArray = new JSONArray();
				for (CrawlHistory ch : crawlHistories){
					JSONArray cha = new JSONArray();
					cha.add(ch.getLink());
					cha.add(ch.getChecksum());
					dataArray.add(cha);
				}
				return dataArray;
			case "students":
				List<Student> students = studentsDAO.selectAll();
				JSONArray dataArrayStu = new JSONArray();
				for (Student s: students){
					JSONArray sa = new JSONArray();
					sa.add(s.getName());
					sa.add(s.getProject());
					sa.add(s.getRoles());
					dataArrayStu.add(sa);
				}
				return dataArrayStu;
			case "supervisor":
				List<Supervisor> supervisors = supervisorDAO.selectAll();
				JSONArray dataArraySuper = new JSONArray();
				for (Supervisor s: supervisors){
					JSONArray sa = new JSONArray();
					sa.add(s.getName());
					sa.add(s.getTeam());
					sa.add(s.getYear());
					sa.add(s.getSemester());
					dataArraySuper.add(sa);
				}
				return dataArraySuper;
			case "links":
				List<Link> links = linksDAO.selectAll();
				JSONArray dataArrayLinks = new JSONArray();
				for (Link s: links){
					JSONArray sa = new JSONArray();
					sa.add(s.getParent());
					sa.add(s.getLink());
					sa.add(s.getType());
					dataArrayLinks.add(sa);
				}
				return dataArrayLinks;
			case "teams":
				List<Team> teams = teamsDAO.selectAll();
				JSONArray dataArrayTeams = new JSONArray();
				for (Team s: teams){
					JSONArray sa = new JSONArray();
					sa.add(s.getName());
					sa.add(s.getMembers());
					sa.add(s.getYear());
					sa.add(s.getSemester());
					sa.add(s.getDescription());
					sa.add(s.getSponsor());
					sa.add(s.getPage());
					sa.add(s.getPitch());
					sa.add(s.getAcceptance());
					sa.add(s.getPoster());
					sa.add(s.getMidterms());
					sa.add(s.getFinals());
					sa.add(s.getDescriptionLinks());
					sa.add(s.getSponsorLinks());
					dataArrayTeams.add(sa);
				}
				return dataArrayTeams;
	    	default:
	    		return null;
			
		}
	}

	private boolean processTableTruncation(ServletContext context,JSONObject jsonDetails) {
		// TODO Auto-generated method stub
		String tableName = (String) jsonDetails.get("table");
		
		CrawlerDAO crawlDAO = (CrawlerDAO)context.getAttribute("crawlerDAO");
		TeamsDAO teamsDAO = (TeamsDAO)context.getAttribute("teamsDAO");
		LinksDAO linksDAO = (LinksDAO)context.getAttribute("linksDAO");
		StudentsDAO studentsDAO = (StudentsDAO)context.getAttribute("studentsDAO");
		SupervisorDAO supervisorDAO = (SupervisorDAO)context.getAttribute("supervisorDAO");
		switch(tableName){
			case "crawler":
				crawlDAO.truncateTable();
				break;
			case "students":
				studentsDAO.truncateTable();
				break;
			case "supervisor":
				supervisorDAO.truncateTable();
				break;
			case "links":
				linksDAO.truncateTable();
				break;
			case "teams":
				teamsDAO.truncateTable();
				break;
			case "all":
				crawlDAO.truncateTable();
				studentsDAO.truncateTable();
				supervisorDAO.truncateTable();
				linksDAO.truncateTable();
				teamsDAO.truncateTable();
				break;
	    	default:
	    		return false;
			
		}
		return true;
	}
}
