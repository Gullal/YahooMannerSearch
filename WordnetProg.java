package parseYahoo;

import java.util.HashSet;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordnetProg
{
	public static void main(String args[])
	{
		WordNetDatabase db = WordNetDatabase.getFileInstance();
		Synset[] synsets = db.getSynsets("swim",SynsetType.NOUN);
		NounSynset ns = null;
		HashSet<String> syns = new HashSet<>();
		
		for(int i=0;i<synsets.length;i++)
		{
			ns = (NounSynset)(synsets[i]);
			syns.add(ns.getWordForms()[0]);
		}
		System.out.println(syns.toString());
	}
}
