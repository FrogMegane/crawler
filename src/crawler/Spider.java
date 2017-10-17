package crawler;
import java.util.*;
import crawler.*;

public class Spider {
	private static final int MAX_PAGES_TO_SEARCH = 50;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = null;
    private List<WebsiteInformation> websites=new ArrayList<WebsiteInformation>();
    
    public void search(String searchWord){
    	System.out.println("搜尋程式Spider開始..");
    	pagesToVisit=SpiderLeg.getLinks(searchWord, MAX_PAGES_TO_SEARCH);
    	boolean firstTime=true;
    	while(pagesVisited.size()<MAX_PAGES_TO_SEARCH && websites.size()<MAX_PAGES_TO_SEARCH && (!pagesToVisit.isEmpty())){
            SpiderLeg leg = new SpiderLeg();
            String currentUrl=nextUrl();       
            pagesVisited.add(currentUrl);
            
            System.out.println("正在拜訪 "+currentUrl);        
            leg.crawl(currentUrl);//抓取htmlDocument檔案           
            List<WebsiteInformation> sites=leg.parseInformation(currentUrl, searchWord);
            if(sites!=null){
            	for(WebsiteInformation tmp:sites){
                	if(tmp!=null){
                		websites.add(tmp);
                	}
                	if(websites.size()>=MAX_PAGES_TO_SEARCH){
                		break;
                	}
                }
            }
            System.out.print("Have visited "+pagesVisited.size()+" pages,");
            System.out.println("and Found "+websites.size()+" links.\n");
        }
    	System.out.println("\n...搜尋程式Spider結束");
    }
    private String nextUrl(){
    	String nextUrl;
        do {
            nextUrl = pagesToVisit.remove(0);
        } while(pagesVisited.contains(nextUrl));//直到找到未拜訪過的網頁
        return nextUrl;
    }
    public List<WebsiteInformation> getWebsites(){
    	return websites;
    }
    
    //在GOOGLE搜尋裡抓資料
    public List<WebsiteInformation> searchForPictures(String keyword){
    	return new SpiderLeg().parsePictureInformation(keyword,MAX_PAGES_TO_SEARCH);
    }
    
}
