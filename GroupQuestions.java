package parseYahoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GroupQuestions extends DefaultHandler
{
    private String temp;
    private String ques;
    private HashMap<String,ArrayList<String>> questions;
    int i = 0;
	
	public GroupQuestions()
	{
		super();
		questions = new HashMap<>();
	}
	
	public static void main(String args[])
	{
		GroupQuestions gq=null;
		try
		{		
			//Create a "parser factory" for creating SAX parsers
            SAXParserFactory spfac = SAXParserFactory.newInstance();

            //Now use the parser factory to create a SAXParser object
            SAXParser sp = spfac.newSAXParser();

            //Create an instance of this class; it defines all the handler methods
            gq = new GroupQuestions();

            //Finally, tell the parser to parse the input and notify the handler
            sp.parse("data/manner.xml", gq);
            
            FileWriter fw = new FileWriter("data/questions.txt");
    		BufferedWriter bw = new BufferedWriter(fw);
    		
    		for(String key: gq.questions.keySet())
    		{
    			bw.write(key+"\n");
    			for(String qs: gq.questions.get(key))
    			{
    				bw.write(qs+"\n");
    			}
    			bw.write("\n\n\n");
    		}
    		
    		bw.close();
    		fw.close();
            
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
		if(qName.equals("subject"))
		{
			ques = "";
		}
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		if(qName.equals("subject"))
			ques = temp;
		else if(qName.equals("maincat"))
		{
			if(!questions.containsKey(temp))
			{
				questions.put(temp, new ArrayList<String>());
			}
			questions.get(temp).add(ques);
		}			
	}
	
	public void characters(char ch[],int start, int length)
	{
		for(int i = start;i<start+length;i++)
		{
			temp = temp + ch[i];
		}
	}
}
