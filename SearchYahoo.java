package parseYahoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashSet;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class SearchYahoo
{
	IndexSearcher indexSearcher = null;
	QueryParser queryParser1, queryParser2, queryParser3;
	WordNetDatabase db = WordNetDatabase.getFileInstance();
	Query query1, query2, query3;
	
	public static void main(String args[]) throws Exception
	{
		SearchYahoo sy = new SearchYahoo();
		sy.startSearch();
		String que = "";
		System.out.println("Enter Query: ");
		
//		long startTime = System.currentTimeMillis();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			que = br.readLine();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		que = que.replace("?","");
		
		String[] words = que.split(" ");
		
		String[] tags = sy.getPosTags(que);
		
		StringBuilder q1 = new StringBuilder();
		
		for (int i=0;i<tags.length;i++)
		{
			HashSet<String> syns = sy.getSynonyms(words[i],tags[i]);
			if(!syns.isEmpty())
			{
				q1.append(words[i]+"^5.0");
				for(String s: syns)
				{
					q1.append(" "+s);
				}
				q1.append(" ");
			}
			if(tags[i].startsWith("N"))
			{
				if(!syns.contains(words[i]))
					q1.append(words[i]+"^5.0");
			}
		}
	    
		TopDocs hits = sy.getTopDocs(q1.toString());
	    
//	    long endTime = System.currentTimeMillis();
	   
//	    System.out.println(hits.totalHits +" documents found. Time :" + (endTime - startTime));
	    
	    for(ScoreDoc sdoc :hits.scoreDocs)
	    {
	    	Document d = sy.indexSearcher.doc(sdoc.doc);
	    	System.out.println("Score: "+sdoc.score);
	    	System.out.println(d.get("subject"));
	    	System.out.println(d.get("answer"));
	    	System.out.println("\n\n");
	    }
	      
	}
	
	public void startSearch() throws Exception
	{
		Directory indexDirectory = FSDirectory.open(Paths.get("indexed/"));
	    indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
	    //indexSearcher.setSimilarity(new BM25Similarity());
	    indexSearcher.setSimilarity(new LMDirichletSimilarity(1000));
//	    queryParser = new MultiFieldQueryParser(
//                new String[] {"subject", "content"},
//                new StandardAnalyzer());
	    queryParser1 = new QueryParser("subject",new StandardAnalyzer());
	    queryParser2 = new QueryParser("content",new StandardAnalyzer());
	    queryParser3 = new QueryParser("answer",new StandardAnalyzer());
	}
	
	public TopDocs getTopDocs(String searchQuery) throws ParseException, IOException
	{
		query1 = queryParser1.parse(searchQuery);
		query2 = queryParser2.parse(searchQuery);
		query3 = queryParser3.parse(searchQuery);
		BooleanQuery finalQuery = new BooleanQuery();
		finalQuery.add(query1, Occur.MUST);
		finalQuery.add(query2, Occur.SHOULD);
		finalQuery.add(query3, Occur.MUST);
	    return indexSearcher.search(finalQuery, 10);
	}
	
	public Document getDocument(ScoreDoc scoreDoc) 
		      throws CorruptIndexException, IOException
	{
		      return indexSearcher.doc(scoreDoc.doc);	
	}
	
	public String[] getPosTags(String input)
	{
		POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
//		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
	 
//		perfMon.start();
	 
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(input);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
 
//		perfMon.incrementCounter();
		
		return tags;
	}
	
	public HashSet<String> getSynonyms(String word,String type)
	{
		Synset[] synsets;
		HashSet<String> syns = new HashSet<>();
		
		if(type.startsWith("N"))
		{
			synsets = db.getSynsets(word,SynsetType.NOUN);
			NounSynset ns;
		
			for(int i=0;i<synsets.length;i++)
			{
				ns = (NounSynset)(synsets[i]);
				if(!ns.getWordForms()[0].contains(" "))
					syns.add(ns.getWordForms()[0].toLowerCase());
			}
		}
		else if(type.startsWith("J"))
		{
			synsets = db.getSynsets(word,SynsetType.ADJECTIVE);
			AdjectiveSynset ads;
		
			for(int i=0;i<synsets.length;i++)
			{
				ads = (AdjectiveSynset)(synsets[i]);
				if(!ads.getWordForms()[0].contains(" "))
					syns.add(ads.getWordForms()[0].toLowerCase());
			}
		}
		else if(type.startsWith("RB"))
		{
			synsets = db.getSynsets(word,SynsetType.ADVERB);
			AdverbSynset as;
		
			for(int i=0;i<synsets.length;i++)
			{
				as = (AdverbSynset)(synsets[i]);
				if(!as.getWordForms()[0].contains(" "))
					syns.add(as.getWordForms()[0].toLowerCase());
			}
		}
		else if(type.startsWith("VB"))
		{
			synsets = db.getSynsets(word,SynsetType.VERB);
			VerbSynset vs;
		
			for(int i=0;i<synsets.length;i++)
			{
				vs = (VerbSynset)(synsets[i]);
				if(!vs.getWordForms()[0].contains(" "))
					syns.add(vs.getWordForms()[0].toLowerCase());
			}
		}
		
		syns.remove(word);
		return syns;
	}
}
