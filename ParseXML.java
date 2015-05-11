package parseYahoo;

import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseXML extends DefaultHandler
{
	private YahooDoc doc;
    private String temp;
    private ArrayList<YahooDoc> docList;
    private ArrayList<String> ansList;
    private ArrayList<String> topicList;
    private IndexWriter writer;
    int i = 0;
	
	public ParseXML()
	{
		super();
		docList = new ArrayList<>();
		topicList = new ArrayList<>();
	}
	
	public static void main(String args[]) throws Exception
	{
		try
		{		
			//Create a "parser factory" for creating SAX parsers
            SAXParserFactory spfac = SAXParserFactory.newInstance();

            //Now use the parser factory to create a SAXParser object
            SAXParser sp = spfac.newSAXParser();

            //Create an instance of this class; it defines all the handler methods
            ParseXML handler = new ParseXML();

            //Finally, tell the parser to parse the input and notify the handler
            sp.parse("data/manner.xml", handler);
            
            handler.indexDocs();
            
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	public void startDocument()
	{
		System.out.println("Start Document");
	}
	
	public void endDocument()
	{
		System.out.println("End Document");
	}
	
	public void startElement(String uri, String name, 
			String qName, Attributes atts) throws SAXException
	{
		temp = "";
		if(qName.equals("document"))
		{
			doc = new YahooDoc();
		}
		if(qName.equals("nbestanswers"))
			ansList = new ArrayList<>();
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		if(qName.equals("document"))
			docList.add(doc);
		else if(qName.equals("uri"))
			doc.setUri(Integer.parseInt(temp));
		else if(qName.equals("subject"))
			doc.setSubject(temp);
		else if(qName.equals("content"))
			doc.setContent(temp);
		else if(qName.equals("bestanswer"))
			doc.setBestAns(temp);
		else if(qName.equals("answer_item"))
			ansList.add(temp);
		else if(qName.equals("nbestanswers"))
			doc.setAnswers(ansList);
		else if(qName.equals("maincat"))
		{
			doc.setCategory(temp);
			if(!topicList.contains(temp))
				topicList.add(temp);
		}
		else if(qName.equals("yid"))
			doc.setYid(temp);
		else if(qName.equals("best_yid"))
			doc.setBestid(temp);
		
	}
	
	public void characters(char ch[],int start, int length)
	{
		for(int i = start;i<start+length;i++)
		{
			temp = temp + ch[i];
		}
	}
	
	public void indexDocs() throws Exception
	{
		Directory indexDirectory = FSDirectory.open(Paths.get("indexed/"));

		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		      
		writer = new IndexWriter(indexDirectory, config);
		
		for(YahooDoc doc: docList)
		{
			Document document = new Document();
			TextField tf = new TextField("subject",doc.getSubject(),Field.Store.YES);
			tf.setBoost(6);
			document.add(tf);
			if(doc.getContent() == null)
				document.add(new TextField("content","",Field.Store.YES));
			else
			{
				TextField tf2 = new TextField("content",doc.getContent(),Field.Store.YES);
				tf2.setBoost(2);
				document.add(tf2);
			}
			if(doc.getBestAns() == null)
				document.add(new TextField("answer","",Field.Store.YES));
			else
				document.add(new TextField("answer",doc.getBestAns(),Field.Store.YES));
			
			writer.addDocument(document);
		}
		
		writer.close();
	}
	
}
