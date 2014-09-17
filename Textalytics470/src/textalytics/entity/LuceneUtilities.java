package textalytics.entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class LuceneUtilities {
	private final static Version LUCENE_VERSION = Version.LUCENE_48;
	private InputStream modelIn = null;
	private ChunkerModel modelChunker = null;
	private POSModel modelPOS = null;
	private TokenizerModel modelTokenizer = null;
	private ParserModel modelParser = null;
	
	public LuceneUtilities() {
		try {
			modelIn = new FileInputStream("C:\\Users\\Melvrick\\Desktop\\IS470\\workspace\\Textalytics470\\WebContent\\bin\\en-chunker.bin");
			modelChunker = new ChunkerModel(modelIn);
			modelIn.close();
			
			modelIn = new FileInputStream("C:\\Users\\Melvrick\\Desktop\\IS470\\workspace\\Textalytics470\\WebContent\\bin\\en-pos-maxent.bin");
			modelPOS = new POSModel(modelIn);
			modelIn.close();
			
			modelIn = new FileInputStream("C:\\Users\\Melvrick\\Desktop\\IS470\\workspace\\Textalytics470\\WebContent\\bin\\en-token.bin");
			modelTokenizer = new TokenizerModel(modelIn);
			modelIn.close();
			
			modelIn = new FileInputStream("C:\\Users\\Melvrick\\Desktop\\IS470\\workspace\\Textalytics470\\WebContent\\bin\\en-parser-chunking.bin");
			System.out.println(modelIn);
			modelParser = new ParserModel(modelIn);
			System.out.println(modelParser);
			modelIn.close();
						
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
			  try {
			    modelIn.close();
			  } catch (IOException e) {}
			}
		}
	}
	
	public String stemmize(String term) throws IOException {

	    // tokenize term
	    TokenStream tokenStream = new ClassicTokenizer(LUCENE_VERSION, new StringReader(term));
	    // stemmize
	    tokenStream = new PorterStemFilter(tokenStream);

	    Set<String> stems = new HashSet<String>();
	    CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
	    // for each token
	    try{
	    	tokenStream.reset();
	    	while (tokenStream.incrementToken()) {
		        // add it in the dedicated set (to keep unicity)
		        stems.add(token.toString());
		    }
	    }finally{
	    	tokenStream.close();
	    }
	    

	    // if no stem or 2+ stems have been found, return null
	    if (stems.size() != 1) {
	        return null;
	    }

	    String stem = stems.iterator().next();

	    // if the stem has non-alphanumerical chars, return null
	    if (!stem.matches("[\\w-]+")) {
	        return null;
	    }

	    return stem;
	}
	
	public HashMap<String,Object> tokenizeAndStemInput(String input) throws IOException {
		String originalInput = input;
		HashMap<String,Object> results = new HashMap<String,Object>();
		
	    // hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
	    input = input.replaceAll("-+", "-0");
	    // replace any punctuation char but dashes and apostrophes and by a space
	    input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
	    // replace most common english contractions
	    input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

	    // tokenize input
	    
	    TokenStream tokenStream = new StandardTokenizer(LUCENE_VERSION, new StringReader(input));
	    // to lower case
	    tokenStream = new LowerCaseFilter(LUCENE_VERSION, tokenStream);
	    // remove dots from acronyms (and "'s" but already done manually above)
	    tokenStream = new ClassicFilter(tokenStream);
	    // convert any char to ASCII
	    tokenStream = new ASCIIFoldingFilter(tokenStream);
	    // remove english stop words
	    tokenStream = new StopFilter(LUCENE_VERSION, tokenStream, EnglishAnalyzer.getDefaultStopSet());
	    
	    List<Keyword> keywords = new LinkedList<Keyword>();
	    CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
	    ArrayList<String> postTokens = new ArrayList<String>();
	    // for each token
	    int topFrequency = 0;
	    try{
	    	tokenStream.reset();
	    	while (tokenStream.incrementToken()) {
		        String term = token.toString();
		        postTokens.add(term);
		        // stemmize
		        String stem = stemmize(term);
		        if (stem != null) {
		            // create the keyword or get the existing one if any
		            Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
		            // add its corresponding initial token
		            keyword.add(term.replaceAll("-0", "-"));
		            //deal with max processed doc/paragraph frequency
		            if (keyword.getFrequency()>topFrequency){
		            	topFrequency = keyword.getFrequency();
		            }
		        }
		    }
	    }finally{
	    	tokenStream.close();
	    }
	    
	    //add in stem term frequency
	    Iterator<Keyword> ki = keywords.iterator();
	    while(ki.hasNext()){
	    	Keyword kw = ki.next();
	    	kw.setTermFrequency(topFrequency);
	    }
	    // reverse sort by frequency
	    Collections.sort(keywords);
	    	    
	    ArrayList<String> keyphrasesResultsList = new ArrayList<String>();
	    NLPParser(originalInput,filterScore(keywords,.4),keyphrasesResultsList);
	    
	    results.put("keywords", keywords);
	    results.put("keyphrases", keyphrasesResultsList);
	    return results;
	}
	
	public String[] POSTagger(String[] tokenizedInput){
		System.out.println(this.modelPOS);
		POSTaggerME post = new POSTaggerME(this.modelPOS);
		return post.tag(tokenizedInput);		
	}
	
	public String[] NLPChunk(String[] tokenizedInput, String[] posTags){
		ChunkerME chunkerME = new ChunkerME(modelChunker);
		return chunkerME.chunk(tokenizedInput, posTags);
	}
	
	private HashMap<String,Keyword> filterScore(List<Keyword> keywords, double wantedScore){
		HashMap<String,Keyword> filteredList = new HashMap<String,Keyword>();
		for (Keyword k : keywords){
			if (k.getTF()<wantedScore){//sorted list, so can stop right after
				break;
			}
			filteredList.put(k.getStem(),k);
		}
		return filteredList;
	}
 	
	public void NLPParser(String input, HashMap<String,Keyword> keywords, ArrayList<String> keyphraseResults){
		Parser parser = ParserFactory.create(modelParser);
		Parse[] topParses = ParserTool.parseLine(input.toLowerCase(), parser, 1);
		List<Parse> nounPhrases = new ArrayList<Parse>();
		List<String> npStrings = new ArrayList<String>();
		
		for (Parse p : topParses){
			p.show();
			getNounPhrases(p,keywords,nounPhrases,keyphraseResults);
		}
		/*for(Parse p: nounPhrases){
			System.out.println(p.getCoveredText() + " " + p.getType());
		}*/
		for(String s:keyphraseResults){
			System.out.println(s);
		}
	}
	
	public void getNounPhrases(Parse p,HashMap<String,Keyword> keywords,List<Parse> nounPhrases,List<String> npStrings) {
		if (p.getType().equals("NP") && p.getChildCount()>0) {
			Parse[] children = p.getChildren();
			
			String phrase = "";
			boolean keyphraseHighFreq = false;
			boolean isOnWhiteList = false;
			boolean isOnBlackList = false;
			for (Parse child: children){
				String stem = "";
				try {
					stem = stemmize(child.getCoveredText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("candidate stem: " + stem);
				//whitelist: app, system, portal
				if(stem!=null){
					switch(stem.toLowerCase()){
						case"app":
							isOnWhiteList =true;break;
						case"system":
							isOnWhiteList =true;break;
						case"portal":
							isOnWhiteList =true;break;
						default:
					}
				}
				if(stem!=null){
					switch(stem.toLowerCase()){
						case"award":isOnBlackList=true;break;
						default:
					}
				}
				if(child.getType().equals("NNS")||child.getType().equals("NN")||child.getType().equals("JJ")||isOnWhiteList){
					if (keywords.containsKey(stem)||isOnWhiteList){
						keyphraseHighFreq = true;
					}
					phrase += child.getCoveredText() + " ";
				}
			}
			phrase = phrase.replaceAll("[^a-zA-Z ]", "").trim();
			int phraseCounter = phrase.trim().split("\\s+").length;
			if (phrase.length()>1 && phraseCounter>1 && keyphraseHighFreq && !npStrings.contains(phrase)){
				//if (!isOnBlackList){
					npStrings.add(phrase);
				//}
			}/*else if (p.getText().length() < 50 && phraseCounter.length()>2){
				npStrings.add(phrase);//targetting very concise snapshots
			}*/
			
	        nounPhrases.add(p);
	    }
	    for (Parse child : p.getChildren()) {
	         getNounPhrases(child,keywords,nounPhrases,npStrings);
	    }
	}
	
	private void recursiveParse(Parse[] parses, HashMap<String,Keyword> keywords, ArrayList<String> keyphraseResults){
		for (Parse p : parses){
			//p.show();
			//System.out.println("child count bigger than 0? " + p.getChildCount());
			if (p.getChildCount()>0){
				recursiveParse(p.getChildren(),keywords,keyphraseResults);
			}else{
				//NP: dealing with noun phrases
				//NN: capture the nouns
				System.out.println("count: "+p.getChildCount() + " text:" + p.getCoveredText() + " tag nodes:" + p.getType() + " head:");
				if (p.getType().equals("NN")){
					//getting the parent
					Parse parent = p.getParent();
					//parent.show();
					System.out.println("parent> "+parent.getText() + " child>"+p.getText()+" tag> " + parent.getType());
					if (parent.getType().equals("NP")){//this is indeed a noun phrase
						try {
							String stem = stemmize(p.getText());
							//System.out.println(stem);
							if (keywords.containsKey(stem)){
								//high frequency keyphrase
								Parse[] children = parent.getChildren();
								String keyphrase = "";
								for (Parse pChild: children){
									if (pChild.getType().equals("JJ")||pChild.getType().equals("NN")){
										keyphrase+=pChild.getText()+" ";
									}
								}
								keyphraseResults.add(keyphrase);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public <T> T find(Collection<T> collection, T example) {
	    for (T element : collection) {
	        if (element.equals(example)) {
	            return element;
	        }
	    }
	    collection.add(example);
	    return example;
	}
}
