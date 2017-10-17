package crawler;
import crawler.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.awt.EventQueue;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JScrollPane;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.awt.event.HierarchyEvent;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

public class Window {
	private final static int resultsPerPage=10;
	private JFrame frame;
	private JTextField keyWordTextBox;
	private final ButtonGroup pageRadioButtonGroup = new ButtonGroup();
	private final ButtonGroup pageOrImage = new ButtonGroup();
	private boolean isImage=false;
	private Box contents;
	private JLabel titles[];
	private JLabel links[];
	private JLabel descriptions[];
	private JRadioButton rdbtnPage1;
	private JRadioButton rdbtnPage2;
	private JRadioButton rdbtnPage3;
	private JRadioButton rdbtnPage4;
	private JRadioButton rdbtnPage5;
	private List<WebsiteInformation> websites;
	private Crawler crawler;
	private final Action action = new SwingAction();
	private JLabel searchHint;
	ImageIcon noImg,loadingImg;
	private JScrollPane scrollPane;
	private JButton searchButton;
	private JRadioButton rdbtnHyperLink;
	private JRadioButton rdbtnImage;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.crawler=new Crawler();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//noImg=new ImageIcon("./noImage.jpg");
		try {
			noImg=new ImageIcon(getClass().getResource("/noImage.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			loadingImg=new ImageIcon(getClass().getResource("/loading.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		frame = new JFrame("爬蟲軟體");
		frame.setBounds(100, 100, 600,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setFont(new Font("Dialog", Font.PLAIN, 13));
		frame.getContentPane().add(verticalBox, BorderLayout.NORTH);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JLabel titleLabel = new JLabel("\u6B61\u8FCE\u4F7F\u7528");
		horizontalBox.add(titleLabel);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_1);
		
		JLabel keyWordLabel = new JLabel("\u95DC\u9375\u5B57\uFF1A\u3000");
		horizontalBox_1.add(keyWordLabel);
		
		keyWordTextBox = new JTextField();
		horizontalBox_1.add(keyWordTextBox);
		keyWordTextBox.setColumns(10);
		
		Box horizontalBox_3 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_3);
		
		JLabel Hint = new JLabel("\u8ACB\u9078\u64C7\uFF1A\u3000");
		horizontalBox_3.add(Hint);
		
		rdbtnHyperLink = new JRadioButton("\u7DB2\u9801");
		rdbtnHyperLink.setSelected(true);
		pageOrImage.add(rdbtnHyperLink);
		horizontalBox_3.add(rdbtnHyperLink);
		
		rdbtnImage = new JRadioButton("\u5716\u7247");
		pageOrImage.add(rdbtnImage);
		horizontalBox_3.add(rdbtnImage);
		
		searchButton = new JButton("\u641C\u5C0B~");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(keyWordTextBox.getText().length()!=0){
					showLoading(true);
					
					SwingWorker<List<WebsiteInformation>,Integer> sw=new SwingWorker<List<WebsiteInformation>,Integer>(){

						@Override
						protected List<WebsiteInformation> doInBackground() throws Exception {
							if(rdbtnHyperLink.isSelected()){
								isImage=false;
								return crawler.search(keyWordTextBox.getText(),false);
							}else if(rdbtnImage.isSelected()){//Images
								isImage=true;
								return crawler.search(keyWordTextBox.getText(),true);
								
							}else{
								return null;
							}
						}
						@Override
						protected void done() {
							rdbtnPage1.setSelected(true);
							showLoading(false);
							try {
								websites=this.get();
							} catch (InterruptedException | ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							update(1);
						}
						
					};
					sw.execute();
					/*原本的code
					if(rdbtnHyperLink.isSelected()){ 
						websites=crawler.search(keyWordTextBox.getText(),false);
						isImage=false;
					}else if(rdbtnImage.isSelected()){//Images					
						websites=crawler.search(keyWordTextBox.getText(),true);
						isImage=true;
					}
					rdbtnPage1.setSelected(true);
					showLoading(false);
					update(1);
					*/
				}
			}
		});
		horizontalBox_3.add(searchButton);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		contents = Box.createVerticalBox();
		scrollPane.setViewportView(contents);
		
		searchHint = new JLabel("**此頁空白**");
		contents.add(searchHint);
		scrollPane.setOpaque(true);
		scrollPane.getViewport().setBackground(Color.WHITE);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox_2, BorderLayout.SOUTH);
		
		JLabel pageLabel = new JLabel("\u9801\u78BC\uFF1A");
		horizontalBox_2.add(pageLabel);
		
		rdbtnPage1 = new JRadioButton("1");
		rdbtnPage1.setAction(action);
		rdbtnPage1.setSelected(true);
		rdbtnPage1.setText("1");
		pageRadioButtonGroup.add(rdbtnPage1);
		horizontalBox_2.add(rdbtnPage1);
		
		rdbtnPage2 = new JRadioButton("2");
		rdbtnPage2.setAction(action);
		rdbtnPage2.setText("2");
		pageRadioButtonGroup.add(rdbtnPage2);
		horizontalBox_2.add(rdbtnPage2);
		
		rdbtnPage3 = new JRadioButton("3");
		rdbtnPage3.setAction(action);
		rdbtnPage3.setText("3");
		pageRadioButtonGroup.add(rdbtnPage3);
		horizontalBox_2.add(rdbtnPage3);
		
		rdbtnPage4 = new JRadioButton("4");
		rdbtnPage4.setAction(action);
		rdbtnPage4.setText("4");
		pageRadioButtonGroup.add(rdbtnPage4);
		horizontalBox_2.add(rdbtnPage4);
		
		rdbtnPage5 = new JRadioButton("5");
		rdbtnPage5.setAction(action);
		rdbtnPage5.setText("5");
		pageRadioButtonGroup.add(rdbtnPage5);
		horizontalBox_2.add(rdbtnPage5);
		
		titles=new JLabel[resultsPerPage];
		descriptions=new JLabel[resultsPerPage];
		links=new JLabel[resultsPerPage];
		for(int i=0;i<resultsPerPage;i++){
			
			titles[i]=new JLabel();
			titles[i].setFont(new Font(Font.DIALOG, Font.BOLD, 20));
			titles[i].setForeground(Color.BLUE);
			titles[i].setVisible(false);
			titles[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
			        Object obj=e.getSource();
			        if(obj instanceof JLabel){
			        	for(int i=0;i<resultsPerPage;i++){
			        		if(obj==titles[i]){
			        			openWebpage(links[i].getText());
			        			break;
			        		}
			        	}
			        }
			    }
			});
			contents.add(titles[i]);
			
			links[i]=new JLabel("");
			links[i].setForeground(new Color(0x00A600));
			links[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
			        Object obj=e.getSource();
			        if(obj instanceof JLabel){
			        	openWebpage(((JLabel)obj).getText());
			        }
			    }
			});
			links[i].setVisible(false);
			contents.add(links[i]);
			
			descriptions[i]=new JLabel();
			descriptions[i].setVisible(false);
			contents.add(descriptions[i]);
			
			contents.add(new JLabel(" "));
		}
	}
	//更新畫面
	private void update(int page){
		if(websites==null){
			websites=new ArrayList<WebsiteInformation>();;
		}
		if(websites.isEmpty()){
			for(int i=0;i<resultsPerPage;i++){
				titles[i].setVisible(false);
				links[i].setVisible(false);
				descriptions[i].setVisible(false);
			}
			searchHint.setText("**此頁空白**");
		}else{
			page=page-1;//index 用
			int size=websites.size();
			searchHint.setText(size+" results found!");
			for(int i=0;i<resultsPerPage;i++){
				int index=page*10+i;
				if(index<size){
					WebsiteInformation tmp=websites.get(index);
					titles[i].setText(tmp.title);
					links[i].setText(tmp.url);
					if(isImage){											
						descriptions[i].setText("");
						if(tmp.icon!=null){
							descriptions[i].setIcon(tmp.icon);
						}else{
							descriptions[i].setIcon(noImg);
						}						
					}else{
						descriptions[i].setIcon(null);
						descriptions[i].setText(tmp.description);
					}			
					
					titles[i].setVisible(true);
					links[i].setVisible(true);
					descriptions[i].setVisible(true);
				}else{
					titles[i].setVisible(false);
					links[i].setVisible(false);
					descriptions[i].setVisible(false);
				}
			}
		}
		scrollPane.getVerticalScrollBar().setValue(0);
		frame.revalidate();
		frame.repaint();
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			Object obj=e.getSource();
			if(obj instanceof JRadioButton){
				update(Integer.parseInt(((JRadioButton)obj).getText()));
			}
			
		}
	}
	public static void openWebpage(String urlString) {
	    try {
	        Desktop.getDesktop().browse(new URL(urlString).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void showLoading(boolean show){
		boolean setting=!show;
		if(show){
			System.out.println("searching...");
			searchHint.setText("第一次搜尋可能會花上1~3分鐘下載(視網路而定)，請稍後...");
			searchHint.setIcon(loadingImg);			
		}else{
			System.out.println("searching...done!");
			searchHint.setText("...完成!");
			searchHint.setIcon(null);
		}
		
		searchButton.setEnabled(setting);
		rdbtnHyperLink.setEnabled(setting);
		rdbtnImage.setEnabled(setting);
		for(int i=0;i<resultsPerPage;i++){
			titles[i].setVisible(setting);
			links[i].setVisible(setting);
			descriptions[i].setVisible(setting);
		}
		rdbtnPage1.setEnabled(setting);
		rdbtnPage2.setEnabled(setting);
		rdbtnPage3.setEnabled(setting);
		rdbtnPage4.setEnabled(setting);
		rdbtnPage5.setEnabled(setting);
		
		frame.revalidate();
		frame.repaint();
	}
}
