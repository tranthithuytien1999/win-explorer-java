package Thigiuaky;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;

public class login {

	public static final String APP_TITLE = "tien";
	private FileSystemView fileSystemView;
	@SuppressWarnings("unused")
	private File currentFile;
	private JPanel gui;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JTable table;
	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;
	@SuppressWarnings("unused")
	private JPanel newFilePanel;
	@SuppressWarnings("unused")
	private JRadioButton newTypeFile;
	@SuppressWarnings("unused")
	private JTextField name;
	private File dirFrom;
	private File dirTo;

	private String nameFile;
	FileOutputStream fos;
	ZipOutputStream zipos;
	FileInputStream fis;
	ZipEntry ze;
	File file;
	ZipInputStream zis;
	JFileChooser fc;
	private JTextField path;
	private JTextField textField;
	List<String> filesListInDir = new ArrayList<String>();

	

	public Container getGui() {

		if (gui == null) {
			gui = new JPanel();
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));

			fileSystemView = FileSystemView.getFileSystemView();
			Desktop.getDesktop();

			JPanel detailView = new JPanel(new BorderLayout(3, 3));

			table = new JTable();
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if(arg0.getClickCount() == 2) {
						File folder = new File(path.getText());
						if(folder.isDirectory()) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
							showChildren(node);
						}
						else if(folder.isFile()) {
							if(!Desktop.isDesktopSupported()){
//					            System.out.println("Desktop is not supported");
					            return;
					        }
					        
					        Desktop desktop = Desktop.getDesktop();
					        if(folder.exists())
								try {
									desktop.open(folder);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
					}
				}
			});
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);

			listSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));
				}
			};
			table.getSelectionModel().addListSelectionListener(listSelectionListener);
			JScrollPane tableScroll = new JScrollPane(table);
			Dimension d = tableScroll.getPreferredSize();

			JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
			detailView.add(fileMainDetails, BorderLayout.NORTH);
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			int count = fileDetailsLabels.getComponentCount();
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add(tableScroll, BorderLayout.CENTER);

			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					showChildren(node);
					setFileDetails((File) node.getUserObject());
				}
			};

			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);

			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}
			gui.setLayout(null);

			JPanel panel = new JPanel();
			panel.setBounds(5, 0, 659, 30);
			gui.add(panel);

			JButton button = new JButton("Back");
			button.addMouseListener(new MouseAdapter() {
				private File back;
				private File previousPath;

				@Override
				public void mouseClicked(MouseEvent e) {
						if(e.getClickCount()==1) {
							back = new File(path.getText());							
							if(back.getParent() == null) {	
								back = new File(path.getText());
							}
							else {
								previousPath = new File(back.getParent());
								DefaultMutableTreeNode node = new DefaultMutableTreeNode(previousPath) ;
								showChildren(node);
								path.setText(back.getParent());
							}
						}
					}
			});
			button.setIcon(
					new ImageIcon(login.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
			button.setSelectedIcon(
					new ImageIcon(login.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
			button.setHorizontalAlignment(SwingConstants.LEFT);
			panel.add(button);

			JButton button_1 = new JButton("Next");
			button_1.addActionListener(new ActionListener() {
				private File next;

				public void actionPerformed(ActionEvent e) {
					String name = path.getText();
					next = new File(name);
					if(next.isDirectory()) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(next);
						showChildren(node);	
					}
				}
			});
			button_1.setIcon(
					new ImageIcon(login.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
			panel.add(button_1);

			path = new JTextField(37);
			path.setHorizontalAlignment(SwingConstants.LEFT);
			panel.add(path);

			textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.LEFT);
			textField.setColumns(13);
			panel.add(textField);

			JButton button_2 = new JButton("Search");
			panel.add(button_2);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);
			splitPane.setBounds(5, 32, 659, 268);
			gui.add(splitPane);

			JButton btnNnFile = new JButton("Zip");
			btnNnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					File dir = new File(path.getText());
					String zipDirName = path.getText() + ".zip";
					login zipFiles = new login();
					zipFiles.zipDirectory(dir, zipDirName);
					JOptionPane.showMessageDialog(null, "Successfully compressed folder");
				}
			});
			btnNnFile.setBounds(322, 302, 81, 23);
			gui.add(btnNnFile);
			
			JButton btnNewButton = new JButton("Open");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File open = new File(path.getText());
					try {
						if(open.isFile()) {
							openFile(open);
						}
						else if(open.isDirectory()) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(open);
							showChildren(node);
						}
						
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			});
			btnNewButton.setBounds(212, 302, 89, 23);
			gui.add(btnNewButton);

		}
		return gui;
	}
	private void zipDirectory(File dir, String zipDirName) {
		try {
			populateFilesList(dir);
			FileOutputStream fos = new FileOutputStream(zipDirName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String filePath : filesListInDir) {
				System.out.println("Zipping " + filePath);
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			fos.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void populateFilesList(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile())
				filesListInDir.add(file.getAbsolutePath());
			else
				populateFilesList(file);
		}
	}
	public static void copyFile(File oldLocation, File newLocation) throws IOException {
        if ( oldLocation.exists( )) {
            BufferedInputStream  reader = new BufferedInputStream( new FileInputStream(oldLocation) );
            BufferedOutputStream  writer = new BufferedOutputStream( new FileOutputStream(newLocation, false));
            try {
                byte[]  buff = new byte[8192];
                int numChars;
                while ( (numChars = reader.read(  buff, 0, buff.length ) ) != -1) {
                    writer.write( buff, 0, numChars );
                }
            } catch( IOException ex ) {
                throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
            } finally {
                try {
                    if ( reader != null ){                      
                        writer.close();
                        reader.close();
                    }
                } catch( IOException ex ){
//                    Log.e(TAG, "Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() ); 
                }
            }
        } else {
            throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() );
        }
    }
	public static void copyFolder(File source, File destination)
    {
        if (source.isDirectory())
        {
            if (!destination.exists())
            {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files)
            {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = null;
            OutputStream out = null;

            try
            {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
	public static void openFile(File path) throws IOException {
		if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }
        
        Desktop desktop = Desktop.getDesktop();
        if(path.exists()) desktop.open(path);
	}
	public void showRootFile() {
		tree.setSelectionInterval(0, 0);
	}

	@SuppressWarnings("unused")
	private TreePath findTreePath(File find) {
		for (int ii = 0; ii < tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile == find) {
				return treePath;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings("unused")
	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					table.setRowHeight(icon.getIconHeight() + rowIconPadding);

					setColumnWidth(0, -1);
					setColumnWidth(3, 60);
					table.getColumnModel().getColumn(3).setMaxWidth(120);

					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			width = (int) preferred.getWidth() + 14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	private void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); // !!
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	@SuppressWarnings("unused")
	private void setFileDetails(File file) {
		currentFile = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		path.setText(file.getPath());

		JFrame f = (JFrame) gui.getTopLevelAncestor();
		if (f != null) {
			f.setTitle(APP_TITLE + " :: " + fileSystemView.getSystemDisplayName(file));
		}

		gui.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				login FileBrowser = new login();
				f.setContentPane(FileBrowser.getGui());

				try {
					URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
					URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add(ImageIO.read(urlBig));
					images.add(ImageIO.read(urlSmall));
					f.setIconImages(images);
				} catch (Exception weTried) {
				}

				f.pack();
				f.setMinimumSize(new Dimension(690, 375));
				f.setVisible(true);

				FileBrowser.showRootFile();
			}
		});
	}

	@SuppressWarnings("serial")
	class FileTableModel extends AbstractTableModel {

		private File[] files;
		private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		private String[] columns = { "Icon", "File", "Path/name", "Size",

		};

		FileTableModel() {
			this(new File[0]);
		}

		FileTableModel(File[] files) {
			this.files = files;
		}

		public Object getValueAt(int row, int column) {
			File file = files[row];
			switch (column) {
			case 0:
				return fileSystemView.getSystemIcon(file);
			case 1:
				return fileSystemView.getSystemDisplayName(file);
			case 2:
				return file.getPath();
			case 3:
				return file.length();
			default:
				System.err.println("Logic Error");
			}
			return "";
		}

		public int getColumnCount() {
			return columns.length;
		}

		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return ImageIcon.class;
			case 3:
				return Long.class;

			}
			return String.class;
		}

		public String getColumnName(int column) {
			return columns[column];
		}

		public int getRowCount() {
			return files.length;
		}

		public File getFile(int row) {
			return files[row];
		}

		public void setFiles(File[] files) {
			this.files = files;
			fireTableDataChanged();
		}
	}

	@SuppressWarnings("serial")
	class FileTreeCellRenderer extends DefaultTreeCellRenderer {

		private FileSystemView fileSystemView;

		private JLabel label;

		FileTreeCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			fileSystemView = FileSystemView.getFileSystemView();
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			File file = (File) node.getUserObject();
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file));
			label.setToolTipText(file.getPath());

			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}
			return label;
		}
	}
}
