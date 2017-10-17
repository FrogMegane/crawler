package crawler;
import java.util.*;
import crawler.*;

public class Spider {
	private static final int MAX_PAGES_TO_SEARCH = 50;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = null;
    private List<WebsiteInformation> websites=new ArrayList<WebsiteInformation>();
    
    public void search(String searchWord){
    	System.out.println("�j�M�{��Spider�}�l..");
    	pagesToVisit=SpiderLeg.getLinks(searchWord, MAX_PAGES_TO_SEARCH);
    	boolean firstTime=true;
    	while(pagesVisited.size()<MAX_PAGES_TO_SEARCH && websites.size()<MAX_PAGES_TO_SEARCH && (!pagesToVisit.isEmpty())){
            SpiderLeg leg = new SpiderLeg();
            String currentUrl=nextUrl();       
            pagesVisited.add(currentUrl);
            
            System.out.println("���b���X "+currentUrl);        
            leg.crawl(currentUrl);//���htmlDocument�ɮ�           
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
    	System.out.println("\n...�j�M�{��Spider����");
    }
    private String nextUrl(){
    	String nextUrl;
        do {
            nextUrl = pagesToVisit.remove(0);
        } while(pagesVisited.contains(nextUrl));//�����쥼���X�L������
        return nextUrl;
    }
    public List<WebsiteInformation> getWebsites(){
    	return websites;
    }
    
    //�bGOOGLE�j�M�̧���
    public List<WebsiteInformation> searchForPictures(String keyword){
    	return new SpiderLeg().parsePictureInformation(keyword,MAX_PAGES_TO_SEARCH);
    }
    
}
