package textalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.ParserFeedback;

@SuppressWarnings("serial")
public class HTMLParseServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		
		try {
			ParserFeedback fb = new DefaultParserFeedback();
			Parser parser = new Parser("https://wiki.smu.edu.sg/is480/Main_Page",fb);
			
			//parsing nodes of 
			for (NodeIterator i = parser.elements (); i.hasMoreNodes (); )
			     processNodes (i.nextNode (),writer);
			
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writer.println("Hello, world");
	}
	
	public void processNodes(Node node, PrintWriter writer){
		if (node instanceof TextNode)
	     {
	         // downcast to TextNode
	         TextNode text = (TextNode)node;
	         // do whatever processing you want with the text
	         String textInformation = text.getText();
	         
	         System.out.println(textInformation);
	         writer.println(textInformation);
	     }
	     if (node instanceof RemarkNode)
	     {
	         // downcast to RemarkNode
	         RemarkNode remark = (RemarkNode)node;
	         String textInformation = remark.getText();
	         // do whatever processing you want with the comment
	         System.out.println(textInformation);
	         writer.println(textInformation);
	     }
	     else if (node instanceof TagNode)
	     {
	         // downcast to TagNode
	         TagNode tag = (TagNode)node;
	         // do whatever processing you want with the tag itself
	         // ...
	         // process recursively (nodes within nodes) via getChildren()
	         NodeList nl = tag.getChildren ();
	         if (null != nl)
				try {
					for (NodeIterator i = nl.elements (); i.hasMoreNodes(); )
						processNodes (i.nextNode (), writer);
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	     }
	}
}
