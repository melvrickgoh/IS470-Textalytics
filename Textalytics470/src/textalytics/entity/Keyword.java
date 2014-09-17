package textalytics.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Keyword implements Comparable<Keyword> {

    private String stem;
    private Integer frequency;
    private double termFrequency;
    private Set<String> terms;
    private String dbTerms;

    public Keyword(String stem) {
        this.stem = stem;
        terms = new HashSet<String>();
        frequency = 0;
        dbTerms = "";
    }
    public Keyword(String stem, HashSet<String> terms, int frequency, double termFrequency){
    	this.stem = stem;
    	this.terms = terms;
    	this.frequency = frequency;
    	this.termFrequency = termFrequency;
    	setTermsDBString();//settle the string freq for db
    }
    public String getStem(){
    	return this.stem;
    }
    private void setTermsDBString(){
    	Iterator<String> termsIt = terms.iterator();
    	this.dbTerms = "";
    	while(termsIt.hasNext()){
    		this.dbTerms+=termsIt.next()+",";
    	}
    	this.dbTerms = this.dbTerms.substring(0,this.dbTerms.length()-1);
    }
    public String getTermsDBString(){
    	return dbTerms;
    }
    public void setTermFrequency(int maxDocFrequency){
    	termFrequency = (double)frequency/maxDocFrequency;
    }
    public double getTF(){
    	return termFrequency;
    }
    
    public int getFrequency(){
    	return frequency;
    }

    public void add(String term) {
        terms.add(term);
        frequency++;
        if (dbTerms.length()>1){
        	dbTerms+=","+term;
        }else{
        	dbTerms+=term;
        }
    }

    @Override
    public int compareTo(Keyword o) {
        return o.frequency.compareTo(frequency);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Keyword && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { stem });
    }

    @Override
    public String toString() {
        return stem + " x" + frequency;
    }

}
