package textalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import textalytics.component.Hasher;
import textalytics.component.TableParser;
import textalytics.dao.LinksDAO;
import textalytics.entity.Link;

@SuppressWarnings("serial")
public class TableServlet extends HttpServlet {
	private TableParser tp = new TableParser();
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		String url = "https://wiki.smu.edu.sg/is480/Past_Teams";
		//File input = new File("\\httrack repo\\index.html");
		Document doc = Jsoup.connect(url).get();
		ServletContext context = req.getSession().getServletContext();
		recordPageLinks(url,doc,context);
		
		//Element lastMod = doc.getElementById("footer-info");
		//Hasher.hash(lastMod.text());
		
		Element pastTeams = doc.getElementById("bodyContent");
		Elements pastLinks = pastTeams.select("[href]");
		
		processLinksParsing(pastLinks,context,writer);
		
		writer.close();	
	}

	private void processLinksParsing(Elements pastLinks, ServletContext context, PrintWriter writer) {
		// TODO Auto-generated method stub
		for (Element e: pastLinks){
			String linkTitle = e.text();
			String linkURL = e.attr("abs:href");
			System.out.println("abs url >> "+e.baseUri());
			
			processTitle(linkTitle.trim(),linkURL.trim(),context,writer);
		}
	}

	private void processTitle(String linkTitle,String linkURL, ServletContext context, PrintWriter writer) {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("^(\\d{4})[-](\\d{4})\\s+(Term\\s+)(\\d{1}).*$");
		Matcher matcher = p.matcher(linkTitle);
		System.out.println(linkTitle + " " + matcher.matches());
		
		if (matcher.matches()){
			int year1 = Integer.parseInt(matcher.group(1));
			int year2 = Integer.parseInt(matcher.group(2));
			int semester = Integer.parseInt(matcher.group(4));
			int year = 0;
			
			if (semester>=2){
				year = year2;
			}else{
				year = year1;
			}
			
			try {
				Document visitDoc = Jsoup.connect(linkURL).get();
				System.out.println("head document " + visitDoc.head().text());
				Elements tables = visitDoc.select("table");
				//parsing the 1st table
				
				//if (year1==2013&&year2==2014&&semester==2){
					Element table = tables.get(0);
					this.tp.parse(year,semester,linkURL,table,context,writer);
					//recordPageLinks(linkURL,visitDoc,context);
				//}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//this.tp.parse(year,semester,url,table,req.getSession().getServletContext(),writer);
		}
	}
	
	private void recordPageLinks(String currentLink, Document doc, ServletContext context){
		Elements links = doc.select("[href]");
		List<Link> linksList = new ArrayList<Link>();
		for (Element linkObj : links){
			linksList.add(new Link(currentLink,linkObj.attr("abs:href"),"link"));
		}
		LinksDAO linksDAO = (LinksDAO) context.getAttribute("linksDAO");
		linksDAO.insertLinks(linksList);
	}
}
