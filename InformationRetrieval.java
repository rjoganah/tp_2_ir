/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationretrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.management.Query.gt;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

/**
 *
 * @author robinjoganah
 */
public class InformationRetrieval {


  public static void main(String[] args) throws IOException, ParseException {
   int choix = 1;
   Scanner scan = new Scanner(System.in);
   System.out.println("Donnez le chemin de la racine de l'applicaiton avec les fichiers textes (ex : /Users/informationRetrieval)");
   String path = scan.nextLine();
   
   while(choix!=0)
   {
       System.out.println("1. Analyzer classique (StopWords inclus et pas de stemming");
       System.out.println("2. Analyzer sans stopwords sans stemming");
       System.out.println("3. Analyzer sans stopwords et avec stemming");
       System.out.println("0.quitter");
       Analyzer analyzer = new StandardAnalyzer();
       switch(scan.nextLine())
       {
           case "1":
               analyzer = new StandardAnalyzer(Version.LATEST, CharArraySet.EMPTY_SET);
               break;
           case "2":
               analyzer = new StandardAnalyzer(Version.LATEST, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
               break;
           case "3":
               analyzer = new EnglishAnalyzer(Version.LATEST,StopAnalyzer.ENGLISH_STOP_WORDS_SET,CharArraySet.EMPTY_SET);
               break;
           case "0":
               choix = 0;
               break;
       }
       
       
       
       

    PrintWriter writer;
    writer = new PrintWriter(path + "/result.txt", "UTF-8");
    
    //
    
    String[] docs;
    

    
    
    String fileQueries = readFile(path + "/list_query.txt");
    String file = readFile(path + "/file_collection.txt");
    docs = file.split(".DocNo \\d");
   
    
   
    Directory index = new RAMDirectory();

    IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
    
    IndexWriter w = new IndexWriter(index, config);
    String titre;
    String texte;
    for(int i=1;i<docs.length;i++)
    {
    	
        //System.out.println();
        final Pattern pattern = Pattern.compile("\\.DocContent\\s+.*");
        final Matcher matcher = pattern.matcher(docs[i]);
        if(matcher.find())
        {
        	
            texte = matcher.group(0);

            final Pattern patternTitre = Pattern.compile("\\.DocTitle\\s+.+");
            final Matcher matcherTitre = patternTitre.matcher(docs[i]);
            if(matcherTitre.find())
            {
                final Pattern patternNumero = Pattern.compile("(\\.DocKey\\s+)(.+)");
                final Matcher matcherNumero = patternNumero.matcher(docs[i]);
                if(matcherNumero.find())
                {
                    addDoc(w, matcherTitre.group(0).substring(3), texte.substring(3),matcherNumero.group(2));
                }
                
                
            
            }
            
        }
        else
        {
        	System.out.println("PATTERN RATE");
        }
            
        
    }
    System.out.println("indexation terminée");

    w.close();
    

    final Pattern pattern = Pattern.compile("(DD15-..?)\\s+(.+)");
    final Matcher matcher = pattern.matcher(fileQueries);
    int k = 1;
    String numero;
    ArrayList<QueryIR> listQueries = new ArrayList<QueryIR>();
    ///Users/robinjoganah/NetBeansProjects
    while(matcher.find())
    {
        QueryIR query = new QueryIR(matcher.group(2),matcher.group(1));
        System.out.println(matcher.group(2));
        listQueries.add(query);
        k++;
        
    }
      


    int hitsPerPage = 200;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    System.out.println("1. TF*IDF");
    System.out.println("2. BMS25");

    switch(scan.nextLine())
       {
           case "1":
               TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
               searcher.setSimilarity(tfidfSIM);
               break;
           case "2":
               searcher.setSimilarity(new BM25Similarity());
               break;
       
               
           
       }
    for(int j = 0; j<listQueries.size();j++)
    {
    
    String querystr = listQueries.get(j).getTexte();


    querystr = QueryParser.escape(querystr);
    Query q = new QueryParser("title", analyzer).parse(querystr);


    
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
     writer.println((listQueries.get(j).getNumero()) + "\t" + d.get("numero") + " " + hits[i].score);

    }
    
    }
    reader.close();
    writer.close();
    try {
          precisionRecall.calculRappel(listQueries,path);
        
      } catch (Throwable ex) {
          System.out.println("error in recall");
      }
    try {
          precisionRecall.calculPrecision(listQueries,path);
          
      } catch (Throwable ex) {
          System.out.println("error in precision");
      }

   }
  }

  private static void addDoc(IndexWriter w, String title, String text,String numero) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));
    doc.add(new TextField("Text", text, Field.Store.YES));
    doc.add(new StringField("numero",numero,Field.Store.YES));
    w.addDocument(doc);
    
  }
  
  private static String readFile(String fio) throws FileNotFoundException{
      try(BufferedReader br = new BufferedReader(new FileReader(fio))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
              return sb.toString();

        
    } catch (IOException ex) {
          Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
}
  

}


