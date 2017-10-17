package crawler;
import crawler.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import net.ucanaccess.jdbc.JackcessOpenerInterface;

import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import java.sql.*;

public class Crawler {	
	
	private final String databasePath;
	private Connection connDB=null;
	
	Crawler(){
		databasePath="./searchResults.accdb";
	}	
	
	//return true if something found
	public List<WebsiteInformation> search(String keyword,boolean forPicture){
		List<WebsiteInformation> toBeReturned=null;
		if(checkForExist(keyword,forPicture)){
			System.out.println("*資料庫中存在該搜尋結果，正在讀取資料...*");
			try{
				toBeReturned=getStoredResults(keyword,forPicture);
				System.out.println("讀取成功!\n");
				return toBeReturned;
			}catch(SQLException e){
				System.out.println("讀取失敗!\n");
			}	
			
		}
		
		System.out.println("開始在網路上搜尋結果...");
		Spider spider=new Spider();
		if(forPicture){
			toBeReturned=spider.searchForPictures(keyword);
		}else{		
			spider.search(keyword);
			toBeReturned=spider.getWebsites();			
		}
		try{
			System.out.println("搜尋完成，正在將資料儲存進資料庫...");
			store(keyword,forPicture,toBeReturned);
			System.out.println("儲存成功!\n");
		}catch(SQLException e){
			System.out.println("儲存失敗!\n");
		}
		closeDB();
		return toBeReturned;
	}
	
	private void store(String keyword,boolean forPicture,List<WebsiteInformation> results) throws SQLException{
		try{
			connectDB();
			Statement st=connDB.createStatement();
			st.executeUpdate("INSERT INTO search (keyword,isPicture) Values ('"+keyword+"',"+forPicture+")");
			st.close();
			
			PreparedStatement ps;
			if(forPicture){
				ps= connDB.prepareStatement(
						  "INSERT INTO results "
						+ "(keyword,isPicture,title,url,description,image) "
						+ "Values(?,?,?,?,?,?)");
			}else{
				ps= connDB.prepareStatement(
						"INSERT INTO results "
						+ "(keyword,isPicture,title,url,description) "
						+ "Values(?,?,?,?,?)");
			}
			for(WebsiteInformation tmp:results){
				ps.setString(1, keyword);
				ps.setBoolean(2, forPicture);
				ps.setString(3, tmp.title);
				ps.setString(4, tmp.url);
				ps.setString(5, tmp.description);
				if(forPicture){
					
					BufferedImage bi = (BufferedImage)tmp.icon.getImage();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					try {
						ImageIO.write(bi, "jpg", os);
					} catch (IOException e) {
						e.printStackTrace();
					}
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					
					try {
						ps.setBinaryStream(6, is, is.available());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}	
	}
	private boolean checkForExist(String keyword,boolean forPicture){
		connectDB();
		try {
			Statement st=connDB.createStatement();
			ResultSet rs=st.executeQuery("SELECT COUNT(*) AS COUNT FROM search "
							+ "WHERE keyword='"+keyword+"'"
							+ "AND isPicture="+forPicture);
			rs.next();
			int count=rs.getInt("COUNT");
			System.out.println(count+" rows in (keyword,forPicture)=("+keyword+","+forPicture+")");
			return count>=1;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return false;
	}
	private List<WebsiteInformation> getStoredResults(String keyword,boolean forPicture)throws SQLException{
		connectDB();
		List<WebsiteInformation> results=new ArrayList<WebsiteInformation>();
		Statement st=connDB.createStatement();
		ResultSet rs=st.executeQuery("SELECT * FROM results "
				+ "WHERE keyword='"+keyword+"'"
				+ "AND isPicture="+forPicture);
		while(rs.next()){
			WebsiteInformation tmp=new WebsiteInformation();
			tmp.url=rs.getString("url");
			tmp.description=rs.getString("description");
			tmp.title=rs.getString("title");
			if(forPicture){
				 InputStream in = null;
	             try {
	            	 in=rs.getBinaryStream("image");
	                 tmp.icon=new ImageIcon(ImageIO.read(in));
	             }catch(IOException e){
	            	 e.printStackTrace();
	             }finally{
	            	if(in!=null){
	            		try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		in=null;
	            	}
	             }
			}
			results.add(tmp);
		}
		return results;
	}
	
	private void connectDB(){
		if(connDB==null){
			try{
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				String dataSource="jdbc:ucanaccess://"+databasePath;
				connDB=DriverManager.getConnection(dataSource);		
				System.out.println("DB linking successed");
			}catch(ClassNotFoundException e){
				System.out.println("Ucanaccess Driver Loading failed");
				e.printStackTrace();
			}catch(SQLException e){
				System.out.println("DB linking failed");
				e.printStackTrace();
			}
		}		
	}
	private void closeDB(){
		if(connDB!=null){
			try {
				connDB.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				connDB=null;
			}
		}
	}
}
