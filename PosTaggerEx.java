package parseYahoo;

import java.io.File;
import java.io.IOException;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class PosTaggerEx
{
	public static void main(String args[]) throws IOException
	{
		POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
	 
		String input = "Are there alternatives to stl in the world out there?";
	 
		perfMon.start();
	 
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(input);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
 
		//POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		
		for(String tag: tags)
			System.out.println(tag);
 
		perfMon.incrementCounter();
			
		perfMon.stopAndPrintFinalResult();
	}
}
