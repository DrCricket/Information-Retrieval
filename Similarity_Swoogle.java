import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

