package textalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import textalytics.dao.StemsDAO;
import textalytics.dao.TeamsDAO;
import textalytics.entity.Keyword;
import textalytics.entity.Link;
import textalytics.entity.LuceneUtilities;
import textalytics.entity.StemRecord;
import textalytics.entity.Team;

@SuppressWarnings("serial")
public class KeyPhraseServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		ServletContext context = req.getSession().getServletContext();
		TeamsDAO teamDAO = (TeamsDAO) context.getAttribute("teamsDAO");
		StemsDAO stemDAO = (StemsDAO) context.getAttribute("stemsDAO");
		LuceneUtilities lu = (LuceneUtilities) context.getAttribute("luceneUtilities");
		
		List<Team> teams = teamDAO.selectAll();
		
		for (Team sampleTeam : teams){
			String description = sampleTeam.getDescription();
			if (description == null || description.trim().length()==0){
				continue;
			}
			System.out.println("------------------"+sampleTeam.getName()+"------------------");
			HashMap<String,Object> results = lu.tokenizeAndStemInput(description);
			List<Keyword> keywords = (List<Keyword>) results.get("keywords");
			List<String> keyphrases = (List<String>) results.get("keyphrases");
			System.out.println(postProcessKeyphrases(keyphrases));
			teamDAO.updateTeamKeyphrases(sampleTeam.getID(), postProcessKeyphrases(keyphrases));
			//stemDAO.insertStemRecords(postProcessKeywords(sampleTeam.getID(),keywords));
		}
		
		writer.close();	
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException{
		doGet(req,resp);
	}
	
	private String postProcessKeyphrases(List<String> keyphrases){
		String result = "";
		for (String k : keyphrases){
			result+=k+",";
		}
		if (result.length()>1){
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	
	private List<StemRecord> postProcessKeywords(int teamID, List<Keyword> keywords){
		List<StemRecord> srs = new ArrayList<StemRecord>();
		for (Keyword k: keywords){
			srs.add(new StemRecord(teamID,k.getStem(),"summary",k));
		}
		return srs;
	}
}
