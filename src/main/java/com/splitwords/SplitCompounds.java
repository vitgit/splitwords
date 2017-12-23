package com.splitwords;

import java.util.*;
import java.util.regex.*; 
import java.io.IOException;
import java.io.BufferedReader; 
import java.io.FileReader;  
import java.io.File;

public class SplitCompounds{
	public static String repeat(String s, int times){    
		if (times <= 0) return "";    
		else if (times % 2 == 0) return repeat(s+s, times/2);    
		else return s + repeat(s+s, times/2); 
	}	
	public static String concatStringsWSep(List<String> strings, String separator){         
		StringBuilder sb = new StringBuilder();         
		for(int i = 0; i < strings.size(); i++){                 
			sb.append(strings.get(i));                 
			if(i < strings.size() - 1)                         
				sb.append(separator);  
		}        
		return sb.toString();                            
	} 
	private static class byLineLength implements java.util.Comparator{
		public int compare(Object one, Object two){
			int sdif = ((String)two).length() - ((String)one).length();
			return sdif;
		}
	}
	public static String getSplit(List<String> dictWords, Map<String, Integer> noSplitWordsMap, String testString, int timesRepeatStart, int timesRepeatEnd){
		String compoundString = testString.toLowerCase();
		if(noSplitWordsMap.containsKey(compoundString) || shouldBeFilteredOut(compoundString))
			return compoundString;
	
		List<String> matches = new ArrayList<String>();
		dictWords.forEach(line -> {
			Pattern p = Pattern.compile(line);
			Matcher m = p.matcher(compoundString);
			if (m.find())
				matches.add(line);	
		});	
		Collections.sort(matches, new byLineLength());
		
		String re1 = concatStringsWSep(matches, "|");
		re1 = "("+re1+")";
		String resultConcatStr = null;
		for (int timesRepeat = timesRepeatStart; timesRepeat <= timesRepeatEnd; timesRepeat++){	
			String re2 = repeat(re1, timesRepeat);
			re2 = "^"+re2+"$";
			Matcher m = Pattern.compile(re2).matcher(compoundString);
			List<String> allMatches = new ArrayList<String>();
			if (m.find()){
				int ii = 0;
				for (ii = 1; ii <= timesRepeat; ii++)
					allMatches.add(m.group(ii));
				if(ii != 0) ii--;
				resultConcatStr = concatStringsWSep(allMatches, "|");
				break;		
			}
		}	
		if (resultConcatStr == null)
			return compoundString;
		else	
			return resultConcatStr;
	}	
	public static List<String> getFileContentToArrayList (File inputFile){
		BufferedReader reader = null;
		List<String> words = new ArrayList<String>(); 
		int lineNum = 0;
		try{
			if (inputFile.exists()){
				reader = new BufferedReader(new FileReader(inputFile));
				String line = reader.readLine().toLowerCase();
				while (line != null){
					line = line.toLowerCase();
					lineNum++;
					line = line.trim();
					line = line.replace("\r","").replace("\n","");
					if(line.length() > 0) words.add(line);
					line = reader.readLine();
				}
				reader.close();
			}
		}
		catch(Throwable t){
			System.out.println("Process at line " + lineNum);
			System.out.println("Error: " + t );
		}
		finally{
			try { 
				if (reader != null) reader.close();
			}
			catch (IOException e) {}			
		}
		return words;
	}		

	public static Map<String, Integer> getFileContentToHashMap (File inputFile){
		List<String> words = getFileContentToArrayList(inputFile);
		Map<String,Integer> wordsHashMap = new HashMap<String,Integer>(words.size()); 
		words.forEach(str -> {wordsHashMap.put(str,1);});
		return wordsHashMap;
	}	
	
	public static boolean shouldBeFilteredOut(String testString){
		if ( ! testString.matches("^[a-zA-Z0-9]+$") || testString.length() < 3 )	
			return true;
		else
			return false;
	}
	
	public static String splitPhrase(List<String> dictWords, Map<String, Integer> noSplitWordsMap, String testPhrase, int timesRepeatStart, int timesRepeatEnd){
		String[] phraseWords = testPhrase.split("\\s+");
		String results = "";
		for (int ii = 0; ii < phraseWords.length; ii++){
			String res = SplitCompounds.getSplit(dictWords, noSplitWordsMap, phraseWords[ii], timesRepeatStart, timesRepeatEnd);
			res = res.replaceAll("\\|", " ");
			results = results.concat(res);
			if (phraseWords.length > 1 && ii < phraseWords.length-1) results = results.concat(" ");
		}		
		return results;
	}
		
	public static void main(String[] args){	
		// i n p u t: ===================
		File inputDictFile = new File("data/single_word_dic_from_fr_50.txt");
		File inputNoSplitFile = new File("data/bag_of_words_bn_kw_ct_all_fr.txt");		
		String testString = "AllInOneAmazingDayCvsPharmacyAirFlightLufthansaCityOfficesUsUkCompoundNyTimesZip01721";
		int timesRepeatStart = 1;
		int timesRepeatEnd = 20;
		// ==============================
		
		List<String> dictWords = getFileContentToArrayList(inputDictFile);
		Map<String, Integer> noSplitWordsMap = getFileContentToHashMap(inputNoSplitFile);
		String resultConcatStr = getSplit(dictWords, noSplitWordsMap, testString, timesRepeatStart, timesRepeatEnd);
		System.out.println(resultConcatStr);
	}
}

