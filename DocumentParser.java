/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.*/

/**
 *
 * @author Dhruv
 */
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class DocumentParser {
    ArrayList docs;
    ArrayList terms;
    Document d;
    HashMap<String,Integer> docFrequency; 
    HashMap<String,Integer> stopList;
    
    DocumentParser(String dir){
        docs=new ArrayList<Document>();
        stopList=new HashMap<String,Integer>();
        docFrequency=new HashMap<String,Integer>();
        handleStopwords(dir);
        parse(dir);
        //System.out.println(docFrequency.toString());
    }
    
    void handleStopwords(String dir){
        BufferedReader br=readFile(dir,"stoplist.txt");
        String currLine;
        try{
            while((currLine=br.readLine())!=null){
                stopList.put(currLine, 1);
            }
            //System.out.println("StopList initialized");
        }catch(Exception e){
            System.out.println("problem with stoplist");
        }
    }
    
    void parse(String Dir){
        BufferedReader br=readFile(Dir,"fileNames.txt");
        String fileToRead;
        BufferedReader brb;
        try{
            while((fileToRead=br.readLine())!=null){
                //System.out.println("file to read is  "+fileToRead);
                brb=readFile(Dir,"files/"+fileToRead);
                d=new Document();
                d.docName=fileToRead;
                //we have the file pointer
                String dummy;
                StringBuffer text=new StringBuffer();
                while((dummy = brb.readLine())!=null){
                    text.append(dummy);
                }
                //System.out.println("text is  "+text);
                
                
               StringTokenizer st=new StringTokenizer(text.toString()," ,-."); 
               String t;
               while(st.hasMoreTokens()){
            	   t=st.nextToken();
            	   if(stopList.get(t)==null){
                       //System.out.println("word to add = "+words[i]);
                       addWord(d,t.toLowerCase());
                   
                   }
               }
                
                
                //System.out.println(d.h.toString());
                docs.add(d);
            }
        }catch(Exception e){
            System.out.println("exception caught in reading the file");
        }
        
    }
    
    
    
    BufferedReader readFile(String Dir,String file){
         BufferedReader br=null;
         try{
             br=new BufferedReader(new FileReader(Dir+"/"+file));
         }catch(Exception e){
             System.out.println("File:"+file+" not found");
         }
        return br;
    }
    
    
    
    void addWord(Document d,String word){
        d.numberOfWords+=1;
        //System.out.println("reached addWord with word = "+word);
        if(d.h.get(word)==null){
            d.h.put(word, 1.00);
            if(docFrequency.get(word)==null){
                docFrequency.put(word, 1);
            }else{
                docFrequency.put(word, docFrequency.get(word)+1);
            }
        }else{
            d.h.put(word, d.h.get(word)+1);
        }
        //System.out.println("added word ");
    }
    
    void parseQuery(String query){
        //we calculate tf-idf here.
        //double nested loop 
        terms=new ArrayList();
        String words[]=query.split(" ");
        int i=0;
        while(i<words.length){
            if(stopList.get(words[i])==null){
                terms.add(words[i]);
            }
            i++;
        }
        //arraylist of query words has been created
        Iterator docItr=docs.iterator();
        double avgdl=0;
        while(docItr.hasNext()){
            d=(Document)docItr.next();
            avgdl+=d.numberOfWords;
        }
        avgdl=avgdl/docs.size();
        docItr=docs.iterator();
        Iterator itr;
        double tf,idf,dmtf;
        double maxtfidf,maxBM25;
        maxtfidf=0;
        maxBM25=0;    
        while(docItr.hasNext()){
            d=(Document)docItr.next();
            itr=terms.iterator();
            String term;
            while(itr.hasNext()){
                term=(String)itr.next();
                //if(d.h.get(term)!=null&&docFrequency.get(term)!=null){
                double val1,val2;
                
                if(d.h.get(term)==null)
                	val1=0;
                else 
                	val1=d.h.get(term);
                
                
                if(docFrequency.get(term)==null)
                	val2=0;
                else 
                	val2=docFrequency.get(term);
                
                    tf=1+Math.log(1.01+val1);
                    
                    
                    idf=1+(double)((docs.size()-val2+0.5)/(val2+0.5));
                    idf=Math.log(idf);
                    //System.out.println("file : "+d.docName+"  term : "+term+ ". term frquency "+val1+". doc frequency : "+val2+". tf value : "+tf+" idf value : "+idf);
                    d.Rank+=tf*idf;
                    
                    dmtf=(val1*(1.5+1))/(val1+1.5*(0.25+0.25*(d.numberOfWords/avgdl)));
                    d.RankBM25+=idf*dmtf;
                //}
                
            }
            
            if(d.Rank>maxtfidf)
                maxtfidf=d.Rank;
            if(d.RankBM25>maxBM25)
                maxBM25=d.RankBM25;
            
        }
        
        docItr=docs.iterator();
        while(docItr.hasNext()){
            d=(Document)docItr.next();
            if(maxBM25==0||maxtfidf==0){
            	//d.Rank=0;
            	d.RankBM25=0;
            }else {
            	
                d.RankBM25=d.RankBM25/maxBM25;
            }
            d.Rank=d.Rank/maxtfidf;
            
            
            //System.out.println("tfidf rank is : "+d.Rank+". bm25 rank is : "+d.RankBM25);
        }
        
        
    }
}
