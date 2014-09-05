package textalytics.component;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import textalytics.dao.CrawlerDAO;
import textalytics.dao.StudentsDAO;
import textalytics.dao.SupervisorDAO;
import textalytics.dao.TeamsDAO;
import textalytics.entity.CrawlHistory;
import textalytics.entity.Student;
import textalytics.entity.Team;

public class TableParser {
	HashMap<Integer,String> titles = new HashMap<Integer,String>();
	HashMap<Integer, HashMap<String, HashMap<String,String>>> rowItems = new HashMap<Integer,HashMap<String,HashMap<String,String>>>();
	PrintWriter writer;
	CrawlerDAO crawlerDAO;
	
	private HashMap<Integer, String> parseTitle(Element titleRow){
		Elements titlesElements = titleRow.getElementsByTag("td");
		for (int i = 0; i<titlesElements.size(); i++){
			String titleText = titlesElements.get(i).text();

			titles.put(i,titleText);
		}
		return titles;
	}
	
	public TableParser(){	}
	
	public void parse(int year, int semester, String link, Element table, ServletContext context, PrintWriter writer){
	    this.writer = writer;
	    this.crawlerDAO = (CrawlerDAO) context.getAttribute("crawlerDAO");
	    TeamsDAO teamsDAO = (TeamsDAO) context.getAttribute("teamsDAO");
	    
	    String checksum = Hasher.hash(table.text());
	    System.out.println("Table Hex : " + checksum);
    	
	    CrawlHistory history = crawlerDAO.getCrawlHistory(link);
	    boolean doParsingToDB = true;
	    boolean firstTime = false;
	    
	    if (history != null && checksum.equalsIgnoreCase(history.getChecksum())){
	    	doParsingToDB = false;
	    }else{
	    	if (history==null){
	    		firstTime = true;
	    	}
	    }
		
	    //process document to db only if the checksum does not match
	    if (doParsingToDB){
	    	StudentsDAO studentDAO = (StudentsDAO)context.getAttribute("studentsDAO");
	    	TeamsDAO teamDAO = (TeamsDAO)context.getAttribute("teamsDAO");
	    	SupervisorDAO supervisorDAO = (SupervisorDAO)context.getAttribute("supervisorDAO");
	    	
	    	Elements rows = table.getElementsByTag("tr");
		    //parse the titles that is meta
		    Element titleRow = rows.get(0);
		    HashMap<Integer,String> meta = parseTitle(titleRow);
		    ArrayList<Team> teams = new ArrayList<Team>();
		    
		    for(int i = 0; i<rows.size(); i++){
		    	if (i==0){
		    		continue;//thats the title
		    	}
		    	Element currentRow= rows.get(i);
		    	Elements columns = currentRow.getElementsByTag("td");
		    	
		    	//last references
		    	String lastSupervisor = "";
		    	String lastReviewer = "";
		    	String dataSupervisor, dataReviewers = "";
	    		HashMap<String,String> dataTeamInfo = null,
	    				dataDescription = null, 
	    				sponsors = null;
	    		ArrayList<String> students = null;

		    	for (int j = 0; j<columns.size(); j++){
		    		Element column = columns.get(j);
		    		String columnPlainText = column.text().trim();
		    		
		    		String columnType = meta.get(j).toLowerCase();
		    		
		    		if (columnType.contains("supervis")){
		    			if (columnPlainText.length()!=0){
		    				lastSupervisor = columnPlainText;
		    			}
		    			dataSupervisor = lastSupervisor;
		    		}else if (columnType.contains("review")){
		    			if (columnPlainText.length()!=0){
		    				lastReviewer = columnPlainText;
		    			}
		    			String[] reviewers = parseReviewers(columnPlainText);
		    			for (String reviewer: reviewers){
		    				dataReviewers += reviewer + ";";
		    			}
		    			dataReviewers = dataReviewers.substring(0,dataReviewers.length()-1);
		    		}else if (columnType.contains("team")){
		    			dataTeamInfo = processTeamInfo(column);
		    		}else if (columnType.contains("proj")){
		    			dataDescription = parseProjectDescription(column);
		    		}else if (columnType.contains("member")){
		    			students = parseMembers(column);
		    		}else if (columnType.contains("spons")){
		    			sponsors = parseSponsors(column);
		    		}else{
		    			print("WTF IS THIS");
		    			print(columnPlainText);
		    		}
		    	}
		    	
		    	//process To Database
		    	String memberNames = "";
		    	//process students
		    	if (students!=null){
		    		List<Student> studentList = new ArrayList<Student>();
		    		for (String s: students){
		    			memberNames+=escapeString(s)+";";
			    		studentList.add(new Student(escapeString(s),dataTeamInfo.get("team"),""));
			    	}
		    		memberNames = memberNames.substring(0,memberNames.length()-1);
		    		
		    		//push to DAO student details
		    		studentDAO.insertStudents(studentList);
		    	}
		    	
		    	String teamName = dataTeamInfo.get("team"),
		    	teamURL = dataTeamInfo.get("teamURL"),
				pitchURL = dataTeamInfo.get("pitch"),
				midTermURL = dataTeamInfo.get("midterm"),
				acceptanceURL = dataTeamInfo.get("acceptance"),
				posterURL = dataTeamInfo.get("poster"),
				finalURL = dataTeamInfo.get("final"),
				fromURL = dataTeamInfo.get("from"),
				projectDescription = dataDescription.get("description"),
				projectDesLinks = dataDescription.get("descriptionLinks"),
				sponsor = sponsors.get("sponsor"),
				sponsorLinks = sponsors.get("sponsorLinks");
		    	
		    	Team t = new Team(teamName,teamURL,pitchURL,acceptanceURL,posterURL,midTermURL,
		    			finalURL,fromURL,projectDescription,memberNames,sponsor,projectDesLinks,sponsorLinks,semester,year);
		    	teams.add(t);
		    	
		    	//Deal with supervisor data
		    	supervisorDAO.insertSupervisorTeamRecord(lastSupervisor, teamName, year, semester);
		    }
		    
		    teamsDAO.insertTeams(teams);
		    
		    //cleaning up, insert if new crawl, update if old crawl
		    if (firstTime){
		    	history = new CrawlHistory(link,checksum);
		    	crawlerDAO.insertCrawlHistory(history);
		    }else{
		    	history.setChecksum(checksum);
		    	crawlerDAO.updateCrawlHistory(history);
		    }
		    
	    }
	}
	
	private String processSupervisor(String s){
		this.writer.println("Supervisor: "+s);
		return s.trim();
	}
	
	private String[] parseReviewers(String s){
		String[] result;
		if (s.contains("&")){
			result = s.split("&");
		}else if (s.contains("and")){
			result = s.split("and");
		}else{
			result = new String[]{s.trim()};
		}
		this.writer.println("Reviewers: "+result.length);
		return result;
	}
	
	private HashMap<String,String> processTeamInfo(Element column){
		HashMap<String,String> dataTeamInfo = new HashMap<String,String>();
		Elements links = column.select("[href]");
		String pitchURLs = "",
		acceptanceURLs = "",
		midtermURLs = "",
		posterURLs = "",
		finalURLs = "",
		fromURLs = "",
		teamName = "",
		teamURL = "";
		
		for (int i = 0; i<links.size(); i++) {
			Element link = links.get(i);
			Element parent = link.parent();
            
            String linkType = classifyLinkType(link,i);
            
            switch(linkType){
            	case "pitch":
            		pitchURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "acceptance":
            		acceptanceURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "midterm":
            		midtermURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "poster":
            		posterURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "final":
            		finalURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "from":
            		fromURLs+=escapeString(link.text())+";"+escapeString(link.attr("abs:href"))+";";
            		break;
            	case "team":
            		teamName = escapeString(link.text());
            		teamURL = escapeString(link.attr("abs:href"));
            		break;
            	default:
            		System.out.println("Failed Type");
            		System.out.println(link.text());
            }
            
            //this.writer.println(parent.text());
			//this.writer.println(String.format(linkType + " * a: <%s>  (%s)", link.attr("abs:href"), link.text()));
        }
		
		if (pitchURLs.length()> 1)
			pitchURLs = pitchURLs.substring(0,pitchURLs.length()-1);
		if (acceptanceURLs.length()>1)
			acceptanceURLs = acceptanceURLs.substring(0,acceptanceURLs.length()-1);
		if (midtermURLs.length()>1)
			midtermURLs = midtermURLs.substring(0,midtermURLs.length()-1);
		if (posterURLs.length()>1)
			posterURLs = posterURLs.substring(0,posterURLs.length()-1);
		if (finalURLs.length()>1)
			finalURLs = finalURLs.substring(0,finalURLs.length()-1);
		if (fromURLs.length()>1)
			fromURLs = fromURLs.substring(0,fromURLs.length()-1);

		dataTeamInfo.put("pitch", pitchURLs);
		dataTeamInfo.put("acceptance", acceptanceURLs);
		dataTeamInfo.put("midterm", midtermURLs);
		dataTeamInfo.put("poster", posterURLs);
		dataTeamInfo.put("final", finalURLs);
		dataTeamInfo.put("from", fromURLs);
		if (teamName.length()<=1){
			teamName=escapeString(column.text().trim());
		}
		dataTeamInfo.put("team", teamName);
		dataTeamInfo.put("teamURL", teamURL);
		return dataTeamInfo;
	}
	
	private String classifyLinkType(Element link, int index){
		String lcURL = link.attr("abs:href").toLowerCase();
		if (index == 0){
			return "team";
		}else if (lcURL.contains("pitch")){
			return "pitch";
		}else if (lcURL.contains("acceptan")){
			return "acceptance";
		}else if (lcURL.contains("midter")||lcURL.contains("mid-ter")){
			return "midterm";
		}else if (lcURL.contains("poster")){
			return "poster";
		}else if (lcURL.contains("final")){
			return "final";
		}else if (lcURL.contains("term")){
			String parentGroup = link.parent().text();
			if (parentGroup.contains("from")||parentGroup.contains("prev")){
				return "from";
			}
			return "term from null";
		}else{
			if (link.text().toLowerCase().contains("poste")){
				return "poster";
			}
		}
			
		return "null";
	}
	
	private HashMap<String,String> parseProjectDescription(Element column){
		//grab links if any
		HashMap<String,String> results = new HashMap<String,String>();
		Elements links = column.select("[href]");
		String linkContent = "";
		
		for (Element link : links) {
            String linkURL = link.attr("abs:href");
            String linkText = escapeString(link.text());
            linkContent+=linkText+";"+linkURL+";";
			//this.writer.println(String.format("Project links * a: <%s>  (%s)", link.attr("abs:href"), link.text()));
        }
		
		if (linkContent.length()>1){
			linkContent = "'"+escapeString(linkContent.substring(0,linkContent.length()-1))+"'";
		}else{
			linkContent = "''";
		}
		String description = escapeString(column.text());

		results.put("description", description);
		results.put("descriptionLinks", linkContent);
		//settle links and description
		return results;
	}
	
	private String escapeString(String s){
		return s.replaceAll("\\'","''");
	}
	
	private ArrayList<String> parseMembers(Element column){
		Elements listMembers = column.select("ol li");
		ArrayList<String> studentNames;
		if (listMembers.size()>0){
			studentNames = getMemberNames(listMembers);
		}else{
			listMembers = column.select("ul li");
			studentNames = getMemberNames(listMembers);
		}
		//do something to store student names
		return studentNames;
	}

	private ArrayList<String> getMemberNames(Elements listMembers) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		this.writer.println("Team Members");
		for (Element e: listMembers){
			list.add(e.text());
			this.writer.println(e.text());
		}
		return list;
	}
	
	private HashMap<String,String> parseSponsors(Element column){
		HashMap<String,String> results = new HashMap<String,String>();
		String text = escapeString(column.text());//total text
		String sponsorLinks = "";
		
		Elements links = column.select("[href]");
		for (Element link: links){
			sponsorLinks+=link.attr("abs:href")+";"+link.text()+";";//link text & url
			this.writer.println(link.attr("abs:href")+";"+link.text());
		}
		if (sponsorLinks.length()>1){
			sponsorLinks = escapeString(sponsorLinks.substring(0,sponsorLinks.length()-1));
			sponsorLinks = "'"+sponsorLinks+"'";
		}else{
			sponsorLinks = "''";
		}
		results.put("sponsorLinks", sponsorLinks);
		if (text.length()==0){
			text="''";
		}
		results.put("sponsor", text);
		
		return results;
	}

	private void print(String msg){
		this.writer.println(msg);
	}
}
