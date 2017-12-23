package com.splitwords;

import java.io.*;
import java.util.List;
import java.util.Map;

public class TestSplitCompounds {
	public static void main(String[] args) throws IOException{		
		// i n p u t: ===================
		File inputDictFile = new File("data/single_word_dic_from_fr_50.txt");
		File inputNoSplitFile = new File("data/bag_of_words_bn_kw_ct_all_fr.txt");
		int timesRepeatStart = 1;
		int timesRepeatEnd = 20;
		// ==============================
		
		List<String> dictWords = SplitCompounds.getFileContentToArrayList(inputDictFile);
		Map<String, Integer> noSplitWordsMap = SplitCompounds.getFileContentToHashMap(inputNoSplitFile);
		
		BufferedReader stdin = new BufferedReader(new InputStreamReader (System.in));	
		
		String testString = "";
		while (! testString.equals("0")) {			
			System.out.print ("Enter a string (0 to quit): ");
			System.out.flush();
			testString = stdin.readLine();

			if (testString.equals("0"))
				System.out.println("Okay, quiting...");
			else {
				String res = SplitCompounds.splitPhrase(dictWords, noSplitWordsMap, testString, 
						timesRepeatStart, timesRepeatEnd);
				System.out.println (res);
			}
		} 
	}
}

