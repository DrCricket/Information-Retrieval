
import java.util.HashMap;
import java.util.Hashtable;
public class Document {
    double Rank;
    int numberOfWords;
    double RankBM25;
    HashMap<String,Double> h;
    //It contains (string,integer) values.String - word and integer stores tf(t,d). 
    String docName;
    Document(){
        h=new HashMap<String,Double>();
        Rank=0;
        numberOfWords=0;
        
    }    
}