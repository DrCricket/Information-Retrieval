import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import javax.swing.*;



class Similarity
{
	private String s1;
	private String s2;
	public double sim;
	
	public Similarity(String s1,String s2) throws IOException
	{
		this.s1 = s1;
		this.s2 = s2;
		calculate();
	}
	
	private void calculate() throws IOException
	{
		String url = "http://swoogle.umbc.edu/SimService/GetSimilarity?operation=api&phrase1="+s1+"&phrase2="+s2;
		
		URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null)
        {
        	if(inputLine.equals("-Infinity"))
        	{
        		sim = 0;
        	}
        	else
        	{sim = Double.parseDouble(inputLine);}
        }
        in.close();
	}
	
	
}



/*************** TAKES A DOCUMENT HASHTABLE && QUERY TERM AS INPUT AND OUTPUTS THESAURUS(SIM) HASHTABLE *******************/
 class QP{
	private String query;
	private double[] arr;
	HashMap<String,Double> similarity_map;
	Similarity s;
	double total_score;
	
	public QP(String query,HashMap<String,Double> hash) throws IOException
	{
		this.query = query;
		calculate(hash);
	}
	
	private void calculate(HashMap<String,Double> hash) throws IOException,NullPointerException
	{
		similarity_map = new HashMap<String,Double>();
		
		for(Entry<String, Double> entry : hash.entrySet()) // For each word in unique hash, find thesaurus
		{
			s = new Similarity(query,entry.getKey());
			String hs = entry.getKey();
			similarity_map.put(hs, s.sim);
		}
		
		for(Map.Entry<String, Double> entry : similarity_map.entrySet()) // Calculate mean thesaurus score
		{
			total_score+=entry.getValue();
		}
		
		total_score/=similarity_map.size();
	}
	
}


 class FuzzyProcessing // I NEED THE TF-IDF OF EACH QUERY TERM AND, BM25 OF EACH QUERY TERM AND LIST OF TERMS IN THE DOCUMENT 
{
	Double final_score;
	Double rank;
	
	public FuzzyProcessing(String query,HashMap<String,Double> hm,Double tf_idf,Double bm_25) throws IOException
	{
		QP qp = new QP(query,hm);
		final_score = qp.total_score*.5 + tf_idf*.25 + bm_25*.25;
		final_score = final_score*100;
		main_processing();
	}
	
	
	void main_processing()
	{
		double low=0.0,medium=0.0 ,high=0.0,very_high=0.0;
		double zero_R=0,low_R=0,medium_R=0,high_R=0;
		
		double zero_a = 0,zero_b = 0,zero_c = 0;
		double low_a=0,low_b=0,low_c=0,low_d=0;
		double medium_a=0,medium_b=0,medium_c=0,medium_d=0;
		double high_a=0,high_b=0,high_c=0;
		
		double CoG_1=0.0,CoG_2=0.0,CoG_3=0.0,CoG_4=0.0;
		
		boolean flag_1=false,flag_2=false,flag_3=false,flag_4=false;
		
		/******************* FUZZIFICATION ******************/
		
		if(final_score < 30)
		{
			low = (30-final_score)/30;
			medium = final_score/30;
		}
		else if(final_score < 65 && final_score > 30)
		{
			medium = (65-final_score)/35;
			high = (final_score-30)/35;
		}
		else
		{
			high = (100-final_score)/35;
			very_high = (final_score-65)/35;
		}
		
		
		/********************** INFERENCE ******************/
		
		if(low!=0.0)
		{
			zero_a = 0.0;
			zero_b = 25 -(25*low);
			zero_c = 25.0;
		}
		if(medium!=0.0)
		{
			low_a = 0.0;
			low_b = 25.0*medium;
			low_c = 75.0 - 50.0*medium;
			low_d = 75.0;
		}
		if(high!=0.0)
		{
			medium_a = 25.0;
			medium_b = 50.0*high-25.0; 
			medium_c = 100.0-25.0*high;
			medium_d = 100.0;
		}
		if(very_high!=0.0)
		{
			high_a = 75;
			high_b = 75 + 25*very_high;
			high_c = 100;
		}
		
		/***************************** DEFUZZIFICATION ******************/
		
		if(low != 0.0)
		{
			CoG_1 = (((zero_b*2 + zero_c)/3) + ((zero_a + zero_b)/2))/2 ;
			flag_1 = true;
		}
		if(medium != 0.0)
		{
			CoG_2 = ((low_a+2*low_b)/3) + ((low_b+low_c)/2) + ((low_c*2+low_d)/3)/3;
			flag_2 = true;
		}
		if(high != 0.0)
		{
			CoG_3 = (((medium_a+2*medium_b)/3) + ((medium_b+medium_c)/2) + ((medium_c*2+medium_d)/3))/3;
			flag_3 = true;
		}
		if(very_high != 0.0)
		{
			CoG_4 = (((high_a+high_b*2)/3) + ((high_b+high_c)/2))/2;
			flag_4 = true;
		}
		
		/************************ ENGINE COMPLETE ************************/
		
		if(flag_1 == true && flag_2 == true)
		{
			rank = (CoG_1+CoG_2)*.5;
		}
		if(flag_2 == true && flag_3 == true)
		{
			rank = (CoG_2+CoG_3)*0.5;
		}
		if(flag_3 == true && flag_4 == true)
		{
			rank = (CoG_3+CoG_4)*0.5;
		}
		return;
	}
	
}



 public class IR_GUI extends JFrame{
	
	static String pathname="";
	static DocumentParser dp = null;
	JTextField result;
	JTextField path;
	IR_GUI()
	{
		super("IR_GUI");
		JPanel p = new JPanel();
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JButton submit = new JButton("Submit");
		
		final JTextArea list = new JTextArea();
		
		JLabel title = new JLabel("Fuzzy Rank",JLabel.CENTER);
		p.setSize(300,300);
		
		
		result = new JTextField(10);
		path = new JTextField(50);
		result.setText("Enter Query Here");
		path.setText("Enter path to files(corpus)");
		submit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				pathname = path.getText();
				IR_GUI.dp=new DocumentParser(pathname);
				String qr = result.getText();
			    dp.parseQuery(qr);
			    Document d = (Document) dp.docs.get(0);
			    FuzzyProcessing fp = null;
			    System.out.println("\n\n\n\n\n");
			    String result_set = "";
			    list.append("RESULT");
				list.append("\n");
				list.append("-------------");
				list.append("\n\n");
				list.setBackground(Color.LIGHT_GRAY);
				list.setForeground(Color.BLACK);
				
			      for(int i=0;i<dp.docs.size();i++)
			      {
			    	  d = (Document) dp.docs.get(i);
			    	  try {
						fp = new FuzzyProcessing(qr,d.h,d.Rank,d.RankBM25);
					} catch (IOException e1) {e1.printStackTrace();}
			          result_set = d.docName+" " +" :"+ fp.rank;
			    	  System.out.println(result_set);
			    	  list.append(result_set);
			    	  list.append("\n");
			       }
			 
				 }
			
		});
		add(p);
		p.setLayout(new BorderLayout());
		
		p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
		p2.setLayout(new GridLayout(4,4));
		p2.add(submit);
		submit.setSize(30,20);
		p1.add(title);
		p1.add(path);
		p1.add(result);
		p3.add(list);
		p.add(BorderLayout.NORTH,p1);
		p.add(BorderLayout.CENTER,p2);
		p.add(BorderLayout.SOUTH,p3);
	}
	
	public static void main(String[] args) {
	    
		
		JFrame jf = new IR_GUI();
		jf.setVisible(true);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setSize(250, 250);
	
	
		
	}
	
}
