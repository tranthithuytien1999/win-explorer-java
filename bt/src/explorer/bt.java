package explorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTree;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class bt extends JFrame {

	private JPanel contentPane;
	private JTextField txtPath;
	private JTextField textField_1;

	public static String CURRENT_FOLDER = "C:\\";
	public static String PREVIOUS_FOLDER = "C:\\";
	
	public DefaultTableModel TABLE_MODEL = new DefaultTableModel(0, 0);
	public String FOLDER_HEADER[] = new String[] { "Name", "Date modified", "Type",
            "Size"};
	private JTable tblFiles;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					bt frame = new bt();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public bt() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 522, 339);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		txtPath = new JTextField();
		panel.add(txtPath);
		txtPath.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.WEST);
		
		JButton btnUndo = new JButton("");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				CURRENT_FOLDER = PREVIOUS_FOLDER;
				TABLE_MODEL = loadFileInFolder(CURRENT_FOLDER, TABLE_MODEL);
        		txtPath.setText(CURRENT_FOLDER);
			}
		});
		btnUndo.setIcon(new ImageIcon(bt.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		panel_3.add(btnUndo);
		
		JButton btnRedo = new JButton("");
		btnRedo.setIcon(new ImageIcon(bt.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		panel_3.add(btnRedo);
		
		textField_1 = new JTextField();
		panel.add(textField_1, BorderLayout.EAST);
		textField_1.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JTextArea txtDetail = new JTextArea();
		txtDetail.setColumns(50);
		panel_1.add(txtDetail);		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JTree tree = new JTree();
		panel_2.add(tree);
		
		JTree tree_1 = new JTree();
		panel_2.add(tree_1);
		
		JTree tree_2 = new JTree();
		panel_2.add(tree_2);
		
		
		//Ä‘Ã¢y lÃ  code cá»§a chÃºng ta
		txtDetail.setText("hello");
		txtPath.setText(CURRENT_FOLDER);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		tblFiles = new JTable();
		tblFiles.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				JTable table =(JTable) e.getSource();
		        Point point = e.getPoint();
		        //int row = table.rowAtPoint(point);
		        int row = table.getSelectedRow();
		        String name = table.getModel().getValueAt(row, 0).toString();
		        if (e.getClickCount() == 2) {
		            //txtDetail.setText(""+name);
		        	PREVIOUS_FOLDER = CURRENT_FOLDER;
		        	CURRENT_FOLDER = CURRENT_FOLDER + name + "\\";
		        	File folder = new File(CURRENT_FOLDER);
		        	if (folder.isDirectory()) {
		        		TABLE_MODEL = loadFileInFolder(CURRENT_FOLDER, TABLE_MODEL);
		        		txtPath.setText(CURRENT_FOLDER);
		        	}
		        }

			}
		});
		scrollPane.setViewportView(tblFiles);
		tblFiles.setModel(TABLE_MODEL);
		TABLE_MODEL.setColumnIdentifiers(FOLDER_HEADER);
		
		TABLE_MODEL = loadFileInFolder(CURRENT_FOLDER, TABLE_MODEL);
		tblFiles.setAutoCreateRowSorter(true);
				

		JPanel zipFilePanel = new JPanel();
		zipFilePanel.setBackground(Color.blue);
		zipFilePanel.setPreferredSize(new Dimension(150, 50));
		contentPane.add(zipFilePanel, BorderLayout.SOUTH);
		zipFilePanel.setLayout(new BorderLayout(0, 0));
		
		JButton zipFileButton = new JButton("Zip file");	
		zipFileButton.setPreferredSize(new Dimension(150, 50));
		zipFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zipFile("D:\\FileNen.txt");
			}          
		});
		zipFilePanel.add(zipFileButton);
	}
	
	public DefaultTableModel loadFileInFolder(String path, DefaultTableModel myTableModel) {
		if (myTableModel.getRowCount() > 0) {
		    for (int i = myTableModel.getRowCount() - 1; i > -1; i--) {
		        myTableModel.removeRow(i);
		    }
		}
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	    	
	    	//{ "Name", "Date modified", "Type","Size"};
	    	File currentFile = listOfFiles[i];
            String name = currentFile.getName();
            String type = "";
            String size = "";
	    	if (listOfFiles[i].isFile()) {
	    		type = "File";
	    		// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
	    		long fileSizeInBytes = currentFile.length();
	    		long fileSizeInKB = fileSizeInBytes / 1024;
	    		size = "" + fileSizeInKB + " kb";
    		} else if (listOfFiles[i].isDirectory()) {
    			type = "Folder";
    		}
	        	
	    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    	String dateModified = sdf.format(currentFile.lastModified());
	    	
	    	myTableModel.addRow(new Object[] { name, dateModified, type, size});
	    }
		return myTableModel;
	}
	
	    public static void compress(String dirPath) {
	        Path sourceDir = Paths.get(dirPath);
	        String zipFileName = dirPath.concat(".zip");
	        try {
	            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
	            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
	                    try {
	                        Path targetFile = sourceDir.relativize(file);
	                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
	                        byte[] bytes = Files.readAllBytes(file);
	                        outputStream.write(bytes, 0, bytes.length);
	                        outputStream.closeEntry();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                    return FileVisitResult.CONTINUE;
	                }
	            });
	            outputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	    public void zipFile(String dirPath) {
	    	byte[] buffer = new byte[1024];
	    	
	    	try {	    		
	    		FileOutputStream fos = new FileOutputStream("D:\\MyZipFile.zip");
	    		ZipOutputStream zos = new ZipOutputStream(fos);
	    		ZipEntry ze= new ZipEntry("MyZipFile");
	    		zos.putNextEntry(ze);
	    		FileInputStream in = new FileInputStream(dirPath);
	   	   
	    		int len;
	    		while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}

	    		in.close();
	    		zos.closeEntry();
	    		zos.close();
	    		
	    		System.out.println("Done");
	    	} catch(IOException ex) {
	    	   ex.printStackTrace();
	    	}
	    }
}

