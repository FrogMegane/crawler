package crawler;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.json.*;

public class SpiderLeg {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	   private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
	    private List<String> links = new LinkedList<String>();
	    private Document htmlDocument;
	    private static final int CONNECTION_TIMEOUT=2000;//http連線時間限制
	    private static final int READ_TIMEOUT=2000;//Image下載時間限制
	    
	    //找一些a[href]
	    public boolean crawl(String url) {
	    	try{
	    		try {
	    			System.out.println("***Call:Thread.sleep(500)，避免被Google封鎖***");
					Thread.sleep(500);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
	    		Connection connection=Jsoup.connect(url).userAgent(USER_AGENT);	    		
	    		Document htmlDocument=connection.get();
	    		this.htmlDocument=htmlDocument;
	    		
	    		if(connection.response().statusCode()==200){
	    			System.out.println("\n**Visting Sucess** Received web page at "+url);
	    		}
	    		if(!connection.response().contentType().contains("text/html")){
	    			System.out.println("**Type Failure** 這個網頁不是html文件");
	    			return false;
	    		}
	    		if(htmlDocument==null){
	    			System.out.println("該網頁為空");
	    			return false;
	    		}
	    		System.out.println("下載成功!");
	    		return true;    		
	    		
	    	}catch(org.jsoup.HttpStatusException e){
	    		e.printStackTrace();
	    		return false;
	    	}catch(IOException e){
	    		e.printStackTrace();
	    		return false;
	    	}catch(IllegalArgumentException e){
	    		e.printStackTrace();
	    		return false;
	    	}
	    }
	    public static List<String> getLinks(String keyword,int amount){
	    	List<String> tmpLinks=new ArrayList<String>();
	    	int numGoogleSearchNeeded=(amount+9)/10;//平均一頁有10個連結
	    	String prefix="https://www.google.com.tw/search?q=";
	    	for(int i=0;i<numGoogleSearchNeeded;i++){
	    		String link=prefix+keyword+"&start="+i+"0";
	    		tmpLinks.add(link);
	    	}
	    	return tmpLinks;
	    }
	    //在body內，找關鍵字
	    public boolean searchForWord(String keyWord){
	    	if(htmlDocument==null){
	    		System.out.println("Warning! the page is empty.");
	    		return false;
	    	}else{
	    		try{
	    			String bodyText=htmlDocument.body().text();
	    			return bodyText.toLowerCase().contains(keyWord.toLowerCase());
	    		}catch(NullPointerException e){
	    			return false;
	    		}		    	
	    	}
	    	
	    }
	    public List<WebsiteInformation> parseInformation(String url,String keyWord){
	    	System.out.print("正在解析: "+url+" ，");
	    	if(htmlDocument==null){
	    		System.out.println("The htmlDocument is empty");
	    		return null;
	    	}
	    	
	    	if(url.contains("https://www.google.com.tw/")){
	    		System.out.println("使用Google專用Parser");
	    		return googleSearchParse(url);//help function
	    	}else{
	    		System.out.println("使用一般Parser");
	    		List<WebsiteInformation> tmpList=new ArrayList<WebsiteInformation>();
		    	WebsiteInformation tmp=new WebsiteInformation();
	    		try{
		    		tmp.url=url;
		    		tmp.title=htmlDocument.title();
		    		
		    		keyWord=keyWord.toLowerCase();
		    		String bodyText=htmlDocument.body().text().toLowerCase();
		    		int index=bodyText.indexOf(keyWord);
		    		tmp.description=bodyText.substring(Math.max(0,index-20),Math.min(bodyText.length(),index+20));
		    		
		    	}catch(RuntimeException e){
		    		e.printStackTrace();
		    	}
	    		tmpList.add(tmp);
	    		return tmpList;
	    	}	    	    	
	    	
	    }
	    private List<WebsiteInformation> googleSearchParse(String curUrl){
	    	if(htmlDocument==null){
	    		return null;
	    	}
	    	List<WebsiteInformation> results=new ArrayList<WebsiteInformation>();
	    	/*Parsing*/
    		Elements elements = htmlDocument.select("div[class=g]");
    		System.out.println(elements.size()+" website(s) found on "+curUrl);
    		
    		for (Element element : elements) {
    			WebsiteInformation tmp=new WebsiteInformation();
    			try{
    				tmp.description=element.select("span[class=st]").text();
        			Element r=element.select("h3[class=r]").first();
        			tmp.title=r.text();
        			Element a=element.select("a").first();
        			tmp.url=a.attr("abs:href");
        			results.add(tmp);
    			}catch(NullPointerException e){
    				e.printStackTrace();
    			}    			
    	    }
    		return results;
	    }   
	    
	    
	    public List<WebsiteInformation> parsePictureInformation(String keyword,int maxCount){
	    	final String url="https://www.google.com.tw/search?q="+keyword+"&tbm=isch";
	    	List<WebsiteInformation> results=new ArrayList<WebsiteInformation>();
	    	try{
	    		Connection connection=Jsoup.connect(url).userAgent(USER_AGENT);
	    		Document htmlDocument=connection.referrer("https://www.google.com/").get();
	    		this.htmlDocument=htmlDocument;
	    		//check
	    		if(connection.response().statusCode()==200){
	    			System.out.println("\n**Visting Sucess** Received web page at "+url);
	    		}
	    		if(!connection.response().contentType().contains("text/html")){
	    			System.out.println("**Type Failure** 這個網頁不是html文件");
	    		}
	    		
	    		/*Parsing*/
	    		Elements elements = htmlDocument.select("div.rg_meta");
	    		System.out.println(elements.size()+" image(s) found!");
	    		
	    		
	    		int count=0;
	    		for (Element element : elements) {
	    			JSONObject root=new JSONObject(element.childNode(0).toString());
	    			WebsiteInformation tmp=new WebsiteInformation();
	    			tmp.description=root.getString("ou");
	    			tmp.title=root.getString("pt");
	    			tmp.url=root.getString("ru");
	    			
	    			//download images
	    			try{		
						System.out.println((++count+1)+" Dowloading "+tmp.description);
						tmp.icon=getImage(tmp.description);
						results.add(tmp);
	    			}catch(IOException e){
	    				tmp.icon=null;
	    			}
					if( results.size() >=maxCount){
	    				break;
	    			}
	    	    }
	    		
	    		
	    	}catch(IOException | JSONException e){
	    		e.printStackTrace();
	    	}
	    	return results;
	    }
	    
	    //下載圖片，在限制的時間內
	    private ImageIcon getImage(String imgUrl) throws IOException{
	        URLConnection con = null;
	        InputStream in = null;
	        try {
	            URL url = new URL(imgUrl);

	            con = url.openConnection();
	            con.setConnectTimeout(CONNECTION_TIMEOUT);
	            con.setReadTimeout(READ_TIMEOUT);
	            in = con.getInputStream();
	            
	            Image img = ImageIO.read(in);
	            if (img != null) {
	                System.out.println("Dowload successed");
	                return new ImageIcon(img);
	            }else{
	            	throw new IOException("Downloaded image is null.");
	            }	            
	        }catch(IOException ioe){
	        	System.out.println("Dowload failed");
	        	ioe.printStackTrace();
	        	throw ioe;
	        }finally {
	            if(in != null) {
	                try {
	                     in.close();
	                } catch(IOException ex) {
	                     // handle close failure
	                }
	            }
	        }
	    }
}
