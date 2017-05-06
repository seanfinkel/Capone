package ca.mcgill.cs.comp303.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.loaders.op.OpenParliamentFileLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.WebParliamentLoader;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.ui.MPView.MPTableView;
import ca.mcgill.cs.comp303.capone.ui.RecommenderView.RecommenderView;
import ca.mcgill.cs.comp303.capone.ui.UserProfileView.UserProfileView;
import ca.mcgill.cs.comp303.capone.user.BinaryExport;
import ca.mcgill.cs.comp303.capone.user.JsonExport;

/**
 * The main class of the program - constructs and displays the GUI.
 * 
 */
@SuppressWarnings("synthetic-access")
public class CaponeUI extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private final String aMpTab = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.mpTab");
	private final String aRecommendTab = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.recommendTab");
	private final String aProfileTab = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.profileTab");
	private final String aDataMenu = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.dataMenu");
	private final String aProfileMenu = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.profileMenu");
	private final String aHelpMenu = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.helpMenu");
	private final String aLoadFromDisk = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.loadFromDisk");
	private final String aLoadFromDiskInstruction = MESSAGES
			.getString("Capone-M2.src.gui.CaponeGUI.loadFromDiskInstruction");
	private final String aAutoLoad = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.autoLoad");
	private final String aAutoLoadTip = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.autoLoadTip");
	private final String aLoadFromWeb = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.loadFromWeb");
	private final String aLoadFromWebInstruction = MESSAGES
			.getString("Capone-M2.src.gui.CaponeGUI.loadFromWebInstruction");
	private final String aUpdateSpeeches = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.updateSpeeches");
	private final String aExportJSON = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.exportJSON");
	private final String aExportJSONtip = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.exportJSONtip");
	private final String aSaveProfile = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.saveProfile");
	private final String aForgetFlag = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.forgetFlag");
	private final String aForgetFlagTip = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.forgetFlagTip");
	private final String aAbout = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.about");
	private final String aAboutTip = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.aboutTip");
	private final String aDeveloped = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.developed");
	private final String aWinter = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.winter");
	private final String aGroup = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.group");
	private final String aAboutCapone = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.aboutCapone");
	private final String aDownloaded = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.downloaded");
	private final String aOfMPs = MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.ofMPs");

	private final Logger aLOGGER = Logger.getLogger(CaponeUI.class.getName());
	private MPTableView aMPPanel;
	private RecommenderView aRecommenderPanel;

	/**
	 * Constructor for the GUI. creates the GUI.
	 */
	public CaponeUI()
	{
		super("capone");

		// Create output file for Logger
		try
		{
			FileHandler fileHandler = new FileHandler("capone-log.txt");
			aLOGGER.addHandler(fileHandler);
		}
		catch (SecurityException e)
		{
			// Couldn't create file log.
			aLOGGER.log(Level.SEVERE, "Couldn't create log file!");
		}
		catch (IOException e)
		{
			// Couldn't create file log.
			aLOGGER.log(Level.SEVERE, "Couldn't create log file!");
		}

		// Check for autoload setting. Load parliament if setting is checked.
		checkAutoLoad(this);

		// Create tabbed interface
		JTabbedPane tabbedPane = new JTabbedPane();

		aMPPanel = new MPTableView();
		tabbedPane.addTab(aMpTab, aMPPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		aRecommenderPanel = new RecommenderView();
		tabbedPane.addTab(aRecommendTab, aRecommenderPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = new UserProfileView();
		tabbedPane.addTab(aProfileTab, panel3);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);

		add(tabbedPane, BorderLayout.CENTER);

		// Create the menubar, and add it to the frame
		JMenuBar menubar = createMenuBar();
		setJMenuBar(menubar);

		// Launch the application!
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// Save user profile before closing
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent pE)
			{
				BinaryExport exporter = new BinaryExport();
				exporter.export(Capone.getInstance().getUserProfile(), "UserProfile");
				System.exit(0);
			}
		});
		pack();
		setVisible(true);

		System.out.println("application Launched...");
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menubar = new JMenuBar();
		JMenu data = new JMenu(aDataMenu);
		JMenu profile = new JMenu(aProfileMenu);
		JMenu help = new JMenu(aHelpMenu);
		data.setMnemonic(KeyEvent.VK_D);
		profile.setMnemonic(KeyEvent.VK_P);
		help.setMnemonic(KeyEvent.VK_H);

		JMenuItem lafdMenuItem = new JMenuItem(aLoadFromDisk);
		lafdMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				// Default starting directory is read from the properties file
				JFileChooser fc;
				String location = Capone.getInstance().getProperties().getDataLocation();
				File f = new File(location);
				if (f.exists())
				{
					fc = new JFileChooser(location);
				}
				// If no directory is specified in properties.txt or if the directory does not exist, use the default
				// system directory
				else
				{
					fc = new JFileChooser();
				}
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle(aLoadFromDiskInstruction);

				// Open the file chooser window
				int returnVal = fc.showOpenDialog(CaponeUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					String directoryPath = file.getPath();

					// Save this as the default location in the properties file
					Capone.getInstance().getProperties().setDataLocation(directoryPath);
					JFrame diskMPLoaderFrame = new JFrame();
					diskMPLoaderFrame.add(new LoadFromDiskProgressMonitor());
					diskMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}

			}
		});

		JCheckBoxMenuItem autoLoadMenuItem = new JCheckBoxMenuItem(aAutoLoad);
		autoLoadMenuItem.setState(Capone.getInstance().getProperties().getAutoLoad());
		autoLoadMenuItem.setToolTipText(aAutoLoadTip);
		autoLoadMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				Boolean autoLoad = Capone.getInstance().getProperties().getAutoLoad();
				// Switch the value of auto load when clicked
				Capone.getInstance().getProperties().setAutoLoad(!autoLoad);
			}
		});

		JMenuItem lafwMenuItem = new JMenuItem(aLoadFromWeb);
		lafwMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				// Default starting directory is read from the properties file
				String location = Capone.getInstance().getProperties().getDataLocation();
				File f = new File(location);
				// If no directory is specified in properties.txt or if the directory does not exist, create a file
				// chooser
				if (!f.exists())
				{
					JFileChooser fc = new JFileChooser(location);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setDialogTitle(aLoadFromWebInstruction);
					// Open the file chooser window
					int returnVal = fc.showOpenDialog(CaponeUI.this);
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file = fc.getSelectedFile();
						String directoryPath = file.getPath();
						// Save this as the default location in the properties file
						Capone.getInstance().getProperties().setDataLocation(directoryPath);

						JFrame webMPLoaderFrame = new JFrame();
						webMPLoaderFrame.add(new LoadFromWebProgressMonitor());
						webMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					}
				}

				else
				{
					JFrame webMPLoaderFrame = new JFrame();
					webMPLoaderFrame.add(new LoadFromWebProgressMonitor());
					webMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}
			}
		});

		JMenuItem updateSpeechesMenuItem = new JMenuItem(aUpdateSpeeches);
		updateSpeechesMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				JFrame webMPLoaderFrame = new JFrame();
				webMPLoaderFrame.add(new LoadSpeechesProgressMonitor());
				webMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			}
		});

		JMenuItem exportMenuItem = new JMenuItem(aExportJSON);
		exportMenuItem.setToolTipText(aExportJSONtip);
		exportMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				// Default starting directory is read from the properties file
				JFileChooser fc;
				String location = Capone.getInstance().getProperties().getJsonPath();
				File f = new File(location);
				if (f.exists())
				{
					fc = new JFileChooser(location);
				}
				// If no directory is specified in properties.txt or if the directory does not exist, use the default
				// system directory
				else
				{
					fc = new JFileChooser();
				}
				fc.setDialogTitle(aSaveProfile);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				// Open the file chooser window
				int returnVal = fc.showSaveDialog(CaponeUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					String filePath = file.getPath();
					// Save this as the default location in the properties file
					Capone.getInstance().getProperties().setJsonPath(filePath);
					JsonExport exp = new JsonExport();
					exp.export(Capone.getInstance().getUserProfile(), filePath);
				}
			}
		});

		JMenuItem forgetMenuItem = new JMenuItem(aForgetFlag);
		forgetMenuItem.setToolTipText(aForgetFlagTip);
		forgetMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				Object[] options = { MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.yes"),
						MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.no") };
				int n = JOptionPane.showOptionDialog(null,
						MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.forgetSpeechesConfirmation"),
						MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.warning"), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

				if (n == JOptionPane.YES_OPTION)
				{
					Capone.getInstance().getUserProfile().clearFlagSpeechList();
				}
			}
		});

		JMenuItem aboutMenuItem = new JMenuItem(aAbout);
		aboutMenuItem.setToolTipText(aAboutTip);
		aboutMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				JOptionPane.showMessageDialog(null, aDeveloped + "McGill University\nCOMP-303 " + aWinter + " - "
						+ aGroup + " 05:\nSean Finkel, Ben Fisher, Cristian Sandru", aAboutCapone,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		data.add(lafdMenuItem);
		data.add(autoLoadMenuItem);
		data.add(lafwMenuItem);
		data.add(updateSpeechesMenuItem);
		profile.add(exportMenuItem);
		profile.add(forgetMenuItem);
		help.add(aboutMenuItem);
		menubar.add(data);
		menubar.add(profile);
		menubar.add(help);
		return menubar;
	}

	/**
	 * Loads all from disk when starting the application when autoload is ON.
	 */
	private void checkAutoLoad(CaponeUI pProgramFrame)
	{
		// When autoload is on, try to load from the location given in properties.txt
		if (Capone.getInstance().getProperties().getAutoLoad())
		{
			// If the no location is given in properties.txt or if the location is not valid,
			// prompt the user to choose a location
			String location = Capone.getInstance().getProperties().getDataLocation();
			File f = new File(location);
			if (!f.exists())
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle(MESSAGES.getString("Capone-M2.src.gui.CaponeGUI.loadFromWebInstruction"));
				
				// Open the file chooser window
				int returnVal = fc.showOpenDialog(pProgramFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					String directoryPath = file.getPath();
					// Save this as the default location in the properties file
					Capone.getInstance().getProperties().setDataLocation(directoryPath);

					JFrame diskMPLoaderFrame = new JFrame();
					diskMPLoaderFrame.add(pProgramFrame.new LoadFromDiskProgressMonitor());
					diskMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					Capone.getInstance().getParliament().notifyObservers();
				}
			}
			else
			{
				JFrame diskMPLoaderFrame = new JFrame();
				diskMPLoaderFrame.add(pProgramFrame.new LoadFromDiskProgressMonitor());
				diskMPLoaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				Capone.getInstance().getParliament().notifyObservers();
			}
		}
		Capone.getInstance().getUserProfile().checkOnMPList();
	}

	/**
	 * checks autoload settings, and creates the gui.
	 * 
	 * @param pArgs
	 *            the the list of command line arguments passed to the program
	 */
	@SuppressWarnings("unused")
	public static void main(String[] pArgs)
	{
		CaponeUI gui = new CaponeUI();
	}

	/**
	 * Class to load data MPs from web and display a progress bar.
	 */
	public class LoadFromWebProgressMonitor extends JPanel implements PropertyChangeListener
	{
		/**
		 * 
		 */
		// private static final Logger LOGGER = Logger.getLogger(LoadFromWebProgressMonitor.class.getName());

		private static final long serialVersionUID = 1L;
		private static final int PERCENT_MIN = 0;
		private static final int PERCENT_MAX = 100;
		private ProgressMonitor aProgressMonitor;
		private WebLoader aWebLoader;

		/**
		 * the WebLoader class will do the "actual work" - it will load MPs from the web, by using WebParliamentLoader.
		 * 
		 */
		class WebLoader extends SwingWorker<Void, Void>
		{
			@Override
			public Void doInBackground()
			{
				Capone.getInstance().getParliament().resetParliament();
				WebParliamentLoader loader = new WebParliamentLoader(Capone.getInstance().getParliament(), Capone
						.getInstance().getProperties().getDataLocation());
				float numberOfMPs;
				try
				{
					Iterator<String> mpLocations = WebParliamentLoader.getMPRelativeLocations();
					numberOfMPs = WebParliamentLoader.getMPListSize();

					// the percentLoaded variable will keep track of the percentage of MPs we've looked at so far.
					float percentLoaded = PERCENT_MIN;
					setProgress(PERCENT_MIN);
					while (mpLocations.hasNext() && !isCancelled())
					{
						// Load the MP, and log the load
						String nextLocation = mpLocations.next();
						String mpKey;
						try
						{
							mpKey = loader.loadMP(nextLocation, Capone.getInstance().getParliament());
							aLOGGER.log(Level.INFO, "Loaded" + mpKey + " from web!");
						}
						catch (FileNotFoundException e)
						{
							aLOGGER.log(Level.WARNING, "Encountered error while loading " + nextLocation + " from web!");
						}
						// update the progress bar with the appropriate percentage.
						percentLoaded += PERCENT_MAX / numberOfMPs;
						setProgress(Math.min((int) percentLoaded, PERCENT_MAX));
					}
				}
				catch (FileNotFoundException e)
				{
					aLOGGER.log(Level.SEVERE, "Encountered error while loading from web!");
				}
				return null;
			}

			@Override
			public void done()
			{
				aProgressMonitor.setProgress(PERCENT_MAX);

				// Once parliament is updated, notify all parliament observers.
				Capone.getInstance().getParliament().notifyObservers();
			}
		}

		/**
		 * The constructor for the progress monitor - creates a new window that will contain the progress bar.
		 */
		public LoadFromWebProgressMonitor()
		{
			super(new BorderLayout());
			aProgressMonitor = new ProgressMonitor(LoadFromWebProgressMonitor.this, aDownloaded, "", PERCENT_MIN,
					PERCENT_MAX);
			aProgressMonitor.setProgress(PERCENT_MIN);
			aWebLoader = new WebLoader();
			aWebLoader.addPropertyChangeListener(this);
			aWebLoader.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent pEvt)
		{
			if ("progress".equals(pEvt.getPropertyName()))
			{
				int progress = (Integer) pEvt.getNewValue();
				aProgressMonitor.setProgress(progress);
				String message = String.format("%d%% " + aOfMPs + ".\n", progress);
				aProgressMonitor.setNote(message);
				if (aProgressMonitor.isCanceled())
				{
					aWebLoader.cancel(true);
				}
			}
		}
	}

	/**
	 * Keeps progress of the % of MPs downloaded in a separate window.
	 * 
	 */
	public class LoadSpeechesProgressMonitor extends JPanel implements PropertyChangeListener
	{
		private static final long serialVersionUID = 1L;
		private static final int PERCENT_MIN = 0;
		private static final int PERCENT_MAX = 100;
		private ProgressMonitor aProgressMonitor;
		private WebLoader aWebLoader;

		/**
		 * the WebLoader class will do the "actual work" - it will load MPs from the web, by using WebParliamentLoader.
		 * 
		 */
		class WebLoader extends SwingWorker<Void, Void>
		{
			@Override
			public Void doInBackground()
			{

				WebParliamentLoader loader = new WebParliamentLoader(Capone.getInstance().getParliament(), Capone
						.getInstance().getProperties().getDataLocation());
				Iterator<MP> mps = Capone.getInstance().getParliament().getMPList().iterator();
				float numberOfMPs = Capone.getInstance().getParliament().getMPList().size();
				// the percentLoaded variable will keep track of the percentage of MPs we've looked at so far.
				float percentLoaded = PERCENT_MIN;
				setProgress(1);
				while (mps.hasNext() && !isCancelled())
				{
					// Load the MP, and log the load
					String currentKey = mps.next().getEmail();
					try
					{
						loader.loadRecentEvents(currentKey, Capone.getInstance().getParliament());
					}
					catch (FileNotFoundException e)
					{
						aLOGGER.log(Level.WARNING, "Error while load speeches for " + currentKey + " from web!");
					}
					// update the progress bar with the appropriate percentage.
					percentLoaded += PERCENT_MAX / numberOfMPs;
					setProgress(Math.min((int) percentLoaded, PERCENT_MAX));
				}
				aLOGGER.log(Level.INFO, "Loaded " + loader.getaNumberOfNewSpeeches() + " new speeches, between "
						+ loader.getaMinDate().toString() + " and " + loader.getaMaxDate().toString());
				return null;
			}

			@Override
			public void done()
			{
				aProgressMonitor.setProgress(PERCENT_MAX);

				// Once parliament is updated, notify all parliament observers.
				Capone.getInstance().getParliament().notifyObservers();
			}
		}

		/**
		 * The constructor for the progress monitor - creates a new window that will contain the progress bar.
		 */
		public LoadSpeechesProgressMonitor()
		{
			super(new BorderLayout());
			aProgressMonitor = new ProgressMonitor(LoadSpeechesProgressMonitor.this, "Downloaded", "", PERCENT_MIN,
					PERCENT_MAX);
			aProgressMonitor.setProgress(PERCENT_MIN);
			aWebLoader = new WebLoader();
			aWebLoader.addPropertyChangeListener(this);
			aWebLoader.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent pEvt)
		{
			if ("progress".equals(pEvt.getPropertyName()))
			{
				int progress = (Integer) pEvt.getNewValue();
				aProgressMonitor.setProgress(progress);
				String message = String.format("%d%% of MPs' speeches.\n", progress);
				aProgressMonitor.setNote(message);
				if (aProgressMonitor.isCanceled())
				{
					aWebLoader.cancel(true);
				}
			}
		}
	}

	/**
	 * Keeps progress of the % of MPs loaded from disk in a separate window.
	 */
	public class LoadFromDiskProgressMonitor extends JPanel implements PropertyChangeListener
	{
		private static final long serialVersionUID = 1L;
		private static final int PERCENT_MIN = 0;
		private static final int PERCENT_MAX = 100;
		private ProgressMonitor aProgressMonitor;
		private DiskLoader aDiskLoader;

		/**
		 * the DiskLoader class will do the "actual work" - it will load MPs from the web, by using WebParliamentLoader.
		 * 
		 */
		class DiskLoader extends SwingWorker<Void, Void>
		{
			@Override
			public Void doInBackground()
			{
				Capone.getInstance().getParliament().resetParliament();
				OpenParliamentFileLoader aLoader = new OpenParliamentFileLoader(Capone.getInstance().getParliament(),
						Capone.getInstance().getProperties().getDataLocation());
				Iterator<String> mpLocations = aLoader.getMPRelativeLocations();
				float numberOfMPs = aLoader.getMPListSize();
				// the percentLoaded variable will keep track of the percentage of MPs we've looked at so far.
				float percentLoaded = PERCENT_MIN;
				setProgress(PERCENT_MIN);
				while (mpLocations.hasNext() && !isCancelled())
				{
					String nextMP = mpLocations.next();
					// Load the MP and his/her recent events
					String mpKey = null;
					try
					{
						mpKey = aLoader.loadMP(nextMP, Capone.getInstance().getParliament());
					}
					catch (FileNotFoundException e)
					{
						aLOGGER.log(Level.WARNING, "Couldn't load MP or speeches for " + nextMP);
					}
					if (mpKey != null)
					{
						try
						{
							aLoader.loadRecentEvents(mpKey, Capone.getInstance().getParliament());
						}
						catch (FileNotFoundException e)
						{
							aLOGGER.log(Level.WARNING, "Couldn't load speeches for " + mpKey);
						}
					}
					// log the load.
					aLOGGER.log(Level.INFO, new Date().toString() + " Loaded " + mpKey + " from location "
							+ Capone.getInstance().getProperties().getDataLocation() + "/politicans/" + nextMP);

					// update the progress bar with the appropriate percentage.
					percentLoaded += PERCENT_MAX / numberOfMPs;
					setProgress(Math.min((int) percentLoaded, PERCENT_MAX));
				}
				return null;
			}

			@Override
			public void done()
			{
				aProgressMonitor.setProgress(PERCENT_MAX);

				// Once parliament is updated, notify all parliament observers.
				//Capone.getInstance().getParliament().notifyObservers();
			}
		}

		/**
		 * The constructor for the progress monitor - creates a new window that will contain the progress bar.
		 */
		public LoadFromDiskProgressMonitor()
		{
			super(new BorderLayout());
			aProgressMonitor = new ProgressMonitor(LoadFromDiskProgressMonitor.this, aDownloaded, "", PERCENT_MIN,
					PERCENT_MAX);
			aProgressMonitor.setProgress(PERCENT_MIN);
			aDiskLoader = new DiskLoader();
			aDiskLoader.addPropertyChangeListener(this);
			aDiskLoader.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent pEvt)
		{
			if ("progress".equals(pEvt.getPropertyName()))
			{
				int progress = (Integer) pEvt.getNewValue();
				aProgressMonitor.setProgress(progress);
				String message = String.format("%d%% " + aOfMPs + ".\n", progress);
				aProgressMonitor.setNote(message);
				if (aProgressMonitor.isCanceled())
				{
					aDiskLoader.cancel(true);
					Capone.getInstance().getParliament().notifyObservers();
				}
			}
		}
	}
}