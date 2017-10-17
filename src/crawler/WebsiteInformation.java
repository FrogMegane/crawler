package crawler;

import javax.swing.ImageIcon;

public class WebsiteInformation {
	public String url;
	public String title;
	public String description;
	public ImageIcon icon=null;
	
	WebsiteInformation(){
		this("None","None","None");
	}
	WebsiteInformation(String url,String title,String description){
		this.url=url;
		this.title=title;
		this.description=description;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof WebsiteInformation){
			WebsiteInformation tmp=(WebsiteInformation)obj;
			return tmp.url==this.url;
		}else{
			return false;
		}
	}
	@Override
	public int hashCode() {
	    return url.hashCode();
	}
	public static WebsiteInformation getPictureInformation(String url,String keyword){
		return new WebsiteInformation();
	}
	public static WebsiteInformation getInformation(String url,String keyword){
		return new WebsiteInformation();
	}
}
