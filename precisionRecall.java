/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationretrieval;
      
import java.io.File;  
import java.io.PrintWriter;  
import java.io.BufferedReader;  
import java.io.FileNotFoundException;
import java.io.FileReader;  
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.search.*;  
import org.apache.lucene.store.*;  
import org.apache.lucene.benchmark.quality.*;  
import org.apache.lucene.benchmark.quality.utils.*;  
import org.apache.lucene.benchmark.quality.trec.*;  

 public class precisionRecall {  

   public static void calculRappel(ArrayList<QueryIR> queries,String path) throws Throwable {  
        float rappel;
        float rappelTotal=0;
        Path currentRelativePath = Paths.get("");
        
        String result = readFile(path + "/result.txt");  
        String pertinence = readFile(path + "/relevant_docs.txt");
        ArrayList<String> reponseAttendues;
         for(int i=0;i<queries.size();i++)
         {
             reponseAttendues = new ArrayList<String>();
             String numeroReq = queries.get(i).getNumero();
             int nombrePertinent=0;
             int nbReponsePertinentes = 0;
             
             final Pattern pattern = Pattern.compile(numeroReq + "\\t([\\d]+)");
             final Matcher matcher = pattern.matcher(pertinence);
             while(matcher.find())
             {
                 nombrePertinent++;
                 
                 reponseAttendues.add(matcher.group(1));
             }
             
             for(int j=0;j<reponseAttendues.size();j++)
             {
                 //System.out.println("REGEX = " + numeroReq + "\t" + reponseAttendues.get(j));
                 final Pattern patternResultat = Pattern.compile(numeroReq + "\t"+reponseAttendues.get(j));
                 final Matcher matcherResultat = patternResultat.matcher(result);
                 if(matcherResultat.find())
                {
                    nbReponsePertinentes++;
                }
             }
             rappel = (float)(nbReponsePertinentes * 1.0/nombrePertinent);
             rappelTotal += rappel;
             //System.out.println("Rappel : " + rappel);
        }
         rappelTotal = rappelTotal / queries.size();
         System.out.println("Rappel Total  : " + rappelTotal);
         
         
  }  
  public static void calculPrecision(ArrayList<QueryIR> queries,String path) throws Throwable {  

        float precision;
        float precisionTotal=0;
       
    
        String result = readFile(path + "/result.txt");  
        String pertinence = readFile(path + "/relevant_docs.txt");
        ArrayList<String> reponseTrouvees;
         for(int i=0;i<queries.size();i++)
         {
             reponseTrouvees = new ArrayList<String>();
             String numeroReq = queries.get(i).getNumero();
             int nombrePertinent=0;
             int nbReponsePertinentes = 0;
             
             final Pattern pattern = Pattern.compile(numeroReq + "\\t([\\d]+)");
             final Matcher matcher = pattern.matcher(result);
             while(matcher.find())
             {
                 nombrePertinent++;
                 
                 reponseTrouvees.add(matcher.group(1));
             }
             
             for(int j=0;j<reponseTrouvees.size();j++)
             {
                 //System.out.println("REGEX = " + numeroReq + "\t" + reponseAttendues.get(j));
                 final Pattern patternResultat = Pattern.compile(numeroReq + "\t"+reponseTrouvees.get(j));
                 final Matcher matcherResultat = patternResultat.matcher(pertinence);
                 if(matcherResultat.find())
                {
                    nbReponsePertinentes++;
                }
             }
             if(nombrePertinent != 0)
                precision = (float)(nbReponsePertinentes * 1.0/nombrePertinent);
             else 
                 precision = 0;
             precisionTotal += precision;
             //System.out.println("Precision : " + precision);
        }
         precisionTotal = precisionTotal / queries.size();
         System.out.println("Precision totale  : " + precisionTotal);
         
         
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