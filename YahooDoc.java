package parseYahoo;

import java.util.ArrayList;

public class YahooDoc
{
	private int uri;
	private String subject;
	private String content;
	private String bestAns;
	private ArrayList<String> answers;
	private String category;
	private String yid;
	private String bestid;
	
	public YahooDoc()
	{
		
	}
	
	public YahooDoc(int uri, String subject, String content, String bestAns,
			ArrayList<String> answers, String category, String yid, String bestid)
	{
		super();
		this.uri = uri;
		this.subject = subject;
		this.content = content;
		this.bestAns = bestAns;
		this.answers = answers;
		this.category = category;
		this.yid = yid;
		this.bestid = bestid;
	}
	
	public int getUri()
	{
		return uri;
	}
	public void setUri(int uri)
	{
		this.uri = uri;
	}
	public String getSubject()
	{
		return subject;
	}
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public String getBestAns()
	{
		return bestAns;
	}
	public void setBestAns(String bestAns)
	{
		this.bestAns = bestAns;
	}
	public ArrayList<String> getAnswers()
	{
		return answers;
	}
	public void setAnswers(ArrayList<String> answers)
	{
		this.answers = answers;
	}
	public String getCategory()
	{
		return category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	public String getYid()
	{
		return yid;
	}
	public void setYid(String yid)
	{
		this.yid = yid;
	}
	public String getBestid()
	{
		return bestid;
	}
	public void setBestid(String bestid)
	{
		this.bestid = bestid;
	}

	@Override
	public String toString()
	{
		return "YahooDoc [uri=" + uri + ",\n subject=" + subject + ",\n content="
				+ content + ",\n bestAns=" + bestAns + ",\n answers=" + answers
				+ ",\n category=" + category + ",\n yid=" + yid + ",\n bestid="
				+ bestid + "]";
	}
		
}
