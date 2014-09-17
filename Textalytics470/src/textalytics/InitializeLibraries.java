package textalytics;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import textalytics.dao.CrawlerDAO;
import textalytics.dao.LinksDAO;
import textalytics.dao.StemsDAO;
import textalytics.dao.StudentsDAO;
import textalytics.dao.SupervisorDAO;
import textalytics.dao.TeamsDAO;
import textalytics.dao.pgDAO;
import textalytics.entity.LuceneUtilities;

public class InitializeLibraries implements ServletContextListener {
	private pgDAO dao;
	private LinksDAO linksDAO;
	private StudentsDAO studentsDAO;
	private TeamsDAO teamsDAO;
	private CrawlerDAO crawlerDAO;
	private SupervisorDAO supervisorDAO;
	private StemsDAO stemsDAO;
	private LuceneUtilities LUCENE_UTILITIES;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Servlet context destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Servlet context initialized");
		dao = new pgDAO();
		linksDAO = new LinksDAO(dao);
		studentsDAO = new StudentsDAO(dao);
		teamsDAO = new TeamsDAO(dao);
		crawlerDAO = new CrawlerDAO(dao);
		supervisorDAO = new SupervisorDAO(dao);
		stemsDAO = new StemsDAO(dao);
		LUCENE_UTILITIES = new LuceneUtilities();
		
		try {
			Connection conn = dao.getConnection();
			System.out.println(conn.getClientInfo());
			conn.close();//close n destroy after getting it
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ServletContext sc = arg0.getServletContext();
		sc.setAttribute("dao", dao);
		sc.setAttribute("linksDAO", linksDAO);
		sc.setAttribute("studentsDAO", studentsDAO);
		sc.setAttribute("teamsDAO", teamsDAO);
		sc.setAttribute("crawlerDAO", crawlerDAO);
		sc.setAttribute("supervisorDAO", supervisorDAO);
		sc.setAttribute("stemsDAO", stemsDAO);
		sc.setAttribute("luceneUtilities", LUCENE_UTILITIES);
	}

}
