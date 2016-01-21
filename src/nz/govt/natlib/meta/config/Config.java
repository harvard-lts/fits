/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  2015 - Minor changes have been made to the original code by Harvard University.
 *  The original Apache license still applies.
 */

package nz.govt.natlib.meta.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.FileUtil;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import blowfishj.BlowfishEasy;

/**
 * The original class has been changed to allow the substitution of a different
 * ClassLoader class in the method
 * private boolean initAdapters(Node node)
 * Since the method was private it could not be overridden.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class Config {

	private static final String ROOT_TAG = "meta-config";

	private static final String INPUT_FILES_TAG = "input-files";

	private static final String HARVESTER_TAG = "harvester";

	private static final String PARAMETER_TAG = "parameter";

	private static final String XML_BASE_TAG = "xml-location";

	private static final String JAR_BASE_TAG = "jar-location";

	private static final String JAR_TAG = "jar";

	private static final String TITLE_TAG = "title";

	private static final String ADMIN_PASSWORD_TAG = "admin-password";

	private static final String ADAPTERS_TAG = "adapters";

	private static final String PROFILES_TAG = "profiles";

	private static final String PROFILE_TAG = "profile";

	private static final String PROFILE_DEFAULT_NAME_TAG = "default";

	private static final String MAPS_TAG = "xslt-map";

	private static final String MAP_TAG = "map";

	private static final String ADAPTER_TAG = "adapter";

	private static final String CLASS_TAG = "class";

	private static final String OUTPUT_DTD_TAG = "output-dtd";

	private static final String INPUT_DTD_TAG = "input-dtd";

	private static final String XSLT_TAG = "xslt";

	private static final String DOC_NAME_TAG = "doc-name";

	private static final String CONFIGURATIONS_TAG = "configurations";

	private static final String CONFIGURATION_TAG = "configuration";

	private static final String NAME_TAG = "name";

	private static final String VALUE_TAG = "value";

	private static final String OUTPUT_DIRECTORY_TAG = "output-directory";

	private static final String DIR_NAME_TAG = "dir";

	private static final String LOG_TAG = "log-dir";

	private static final String URL_TAG = "url";

	private static final String USERS_TAG = "users";

	private static final String USER_TAG = "user";

	private static final String USER_NAME_TAG = "name";

	private static final String USER_DEFAULT_NAME_TAG = "default";

	private static final int USER_ADDED = 0;

	private static final int USER_REMOVED = 1;

	private static final int PROFILE_ADDED = 0;

	private static final int PROFILE_REMOVED = 1;

	private static final int PROFILE_CHANGED = 2;

	private static final String SEED = "9B73B749C6A2EA4112400B66EB1E762F";

	public static final String SYSTEM_ADAPTER = "[SYSTEM]";

	private static Config instance;

	private static Config editInstance;
	
	// custom ClassLoader; system ClassLoader if none set.
	private static ClassLoader classLoader;

	private ArrayList configMapping;

	private ArrayList users;

	private ArrayList profiles;

	private Profile defaultProfile;

	private Profile currentProfile;

	private Node usersNode;

	private Node baseXMLDir;

	private Node baseJarDir;

	private Node appName;

	private Node adaptersNode;

	private Node adminPasswordNode;

	private static final String copyright = '\u00A9' + " National Library of New Zealand";

	private Node baseHarvestDir;

	private Node logDirectory;

	private Node mapNode;

	private Node profileNode;

	private Node configNode;

	private Document configDoc;

	private User defaultUser;

	private ArrayList configs = new ArrayList();

	private HashMap adapterJarLocations = new HashMap();

	private HashSet userListeners = new HashSet();

	private HashSet profileListeners = new HashSet();

	private String adminPassword;

	private Config() {
		configMapping = new ArrayList();
		users = new ArrayList();
		readConfig();
	}

	public void setJarForAdapter(DataAdapter adapter, String jar) {
		adapterJarLocations.put(adapter.getClass(), jar);
	}

	public String getJarForAdapter(DataAdapter adapter) {
		return (String) adapterJarLocations.get(adapter.getClass());
	}

	private Config(Config original) {
		configMapping = (ArrayList) original.configMapping.clone();
		users = (ArrayList) original.users.clone();
		profiles = new ArrayList();
		currentProfile = original.currentProfile;
		defaultProfile = original.defaultProfile;
		defaultUser = original.defaultUser;
		Iterator it = original.profiles.iterator();
		while (it.hasNext()) {
			Profile origP = (Profile) it.next();
			Profile p = new Profile();
			p.setName(origP.getName());
			p.setInputDirectory(origP.getInputDirectory());
			p.setLogDirectory(origP.getLogDirectory());
			Iterator ads = origP.getAdapterClasses();
			while (ads.hasNext()) {
				p.setAdapter((String) ads.next(), true);
			}
			baseXMLDir = original.baseXMLDir;
			baseJarDir = original.baseJarDir;
			profiles.add(p);
			if (original.currentProfile.getName().equals(p.getName())) {
				currentProfile = p;
			}
			if (original.defaultProfile.getName().equals(p.getName())) {
				defaultProfile = p;
			}
		}
		configs = (ArrayList) original.configs.clone();
		adapterJarLocations = (HashMap) original.adapterJarLocations.clone();
	}

	public static synchronized void saveAdapterEdit() {
		instance.adapterJarLocations = editInstance.adapterJarLocations;
		instance.configMapping = editInstance.configMapping;
	}

	public static synchronized void saveEdit() {
		instance.configMapping = editInstance.configMapping;
		instance.users = editInstance.users;
		instance.profiles = editInstance.profiles;
		instance.defaultProfile = editInstance.defaultProfile;
		instance.defaultUser = editInstance.defaultUser;
		Profile old = instance.currentProfile;
		instance.currentProfile = editInstance.currentProfile;
		if (!old.equals(instance.currentProfile)) {
			instance.fireProfileEvent(PROFILE_CHANGED, old);
		}
		instance.configs = editInstance.configs;
		instance.adapterJarLocations = editInstance.adapterJarLocations;
	}

	public synchronized static Config getInstance() {
		if (classLoader == null) {
			// If no class loader set use default -- the system class loader.
			Config.classLoader = ClassLoader.getSystemClassLoader();
		}
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	/**
	 * Allow for the configuration of a ClassLoader other than the
	 * default system class loader for loading the Adapter classes
	 * registered in config.xml.
	 * 
	 * @param cl - An alternative, custom class loader.
	 * @see initAdapters(Node)
	 */
	public synchronized static void setClassLoader(final ClassLoader cl) {
		if (cl != null) {
			Config.classLoader = cl;
		}
	}

	public synchronized static Config getEditInstance(boolean renew) {
		if ((editInstance == null) || renew) {
			editInstance = new Config(Config.getInstance());
		}
		return editInstance;
	}

	public synchronized static Config getEditInstance() {
		return getEditInstance(false);
	}

	public ArrayList getAvailableConfigs() {
		return configs;
	}
	
	/**
	 * Get the configuration with a given name.
	 * @param name The name of the configuration to retrieve.
	 * @return The Configuration object.
	 */
	public Configuration getConfiguration(String name) throws ConfigurationException {
		Iterator it = configs.iterator();
		while (it.hasNext()) {
			Configuration c = (Configuration) it.next();
			if (c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		
		// We haven't found the configuration, return null.
		throw new ConfigurationException("Could not find a configuration with the name " + name);
	}

	public ArrayList getAvailableProfiles() {
		return profiles;
	}

	public Profile getDefaultProfile() {
		return defaultProfile;
	}

	public void setAdminPassword(String oldPassword, String password) {
		if (oldPassword.equals(adminPassword)) {
			adminPassword = password;
			BlowfishEasy bfish = new BlowfishEasy(SEED.toCharArray());
			adminPasswordNode.setNodeValue(bfish.encryptString(password));
		}
	}

	public boolean checkAdminPassword(String pass) {
		if (pass != null) {
			return pass.equals(adminPassword);
		}
		return false;
	}

	public Profile getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(Profile current) {
		currentProfile = current;
	}

	public void addConfig(Configuration config) {
		configs.add(config);
	}

	public User getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(User user) {
		this.defaultUser = user;
	}

	public String getBaseHarvestDir() {
		return getCurrentProfile().getInputDirectory();
	}

	public void setBaseHarvestDir(String baseHarvestDir) {
		getCurrentProfile().setInputDirectory(baseHarvestDir);
	}

	public String getXMLBaseURL() {
		String st = baseXMLDir.getNodeValue();
		try {
			File f = new File(st);
			return f.getAbsolutePath();
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(ex);
		}
		return st;
	}

	public String getJarBaseURL() {
		String st = baseJarDir.getNodeValue();
		try {
			File f = new File(st);
			return f.getAbsolutePath();
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(ex);
		}
		return st;
	}

	public void setXMLBaseURL(String url) {
		baseXMLDir.setNodeValue(url);
	}

	public void setJarBaseURL(String url) {
		baseJarDir.setNodeValue(url);
	}

	public String getApplicationName() {
		return appName.getNodeValue();
	}

	public void setApplicationName(String name) {
		appName.setNodeValue(name);
	}

	public String getCopyright() {
		return copyright;
	}

	public void setLogDirectory(String logDirectory) {
		instance.getCurrentProfile().setLogDirectory(logDirectory);
	}

	public String getLogDirectory() {
		return instance.getCurrentProfile().getLogDirectory();
	}

	public void addMapping(String inputDTD, String outputDTD,
			String xsltFunction) {
		configMapping
				.add(new ConfigMapEntry(inputDTD, outputDTD, xsltFunction));
	}

	public String createLogFileName() {
		DateFormat formatter = new SimpleDateFormat("MMMdyyyy_Hmmss");
		Date now = new Date();
		String nowText = formatter.format(now);
		return getLogDirectory() + "/nlnz_" + nowText + ".log";
	}

	public ConfigMapEntry getMapping(String inputDTD, String outputDTD) {
		Iterator it = configMapping.iterator();
		while (it.hasNext()) {
			ConfigMapEntry entry = (ConfigMapEntry) it.next();
			if (entry.getInputDTD().equals(inputDTD)
					&& entry.getOutputDTD().equals(outputDTD)) {
				return entry;
			}
		}
		return null; // no function exists to do this transform...
	}

	public ConfigMapEntry[] getMappings() {
		ConfigMapEntry[] maps = new ConfigMapEntry[configMapping.size()];
		configMapping.toArray(maps);
		return maps;
	}

	public void addMapping(ConfigMapEntry mapping) {
		configMapping.add(mapping);
	}

	public void removeMapping(ConfigMapEntry mapping) {
		configMapping.remove(mapping);
	}

	public void removeConfig(Configuration config) {
		configs.remove(config);
	}

	public User[] getUsers() {
		User[] u = new User[users.size()];
		users.toArray(u);
		return u;
	}

	public void writeNewConfig() throws IOException {
		// find out where...
		URL furl = classLoader.getResource("saveconfig.xml");
		// remove spaces - if any...
		String configPath = URLDecoder.decode(furl.getPath(), "UTF-8");
		File configFile = new File(configPath);
		File backup = new File(configFile.getPath() + ".bak");
		// backup - if poss
		boolean backedup = (backup.delete() && configFile.renameTo(backup));
		if (!backedup) {
			LogMessage msg = new LogMessage(LogMessage.INFO, configFile,
					"Config File not backed up",
					"check file system permissions etc");
			LogManager.getInstance().logMessage(msg);
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			// Prepare the DOM document for writing
			Source source = new DOMSource(configDoc);

			// Prepare the output file
			Result result = new StreamResult(configFile);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new RuntimeException(ex);
		}

	}

	public void writeConfig() throws IOException {

		// find out where...
		URL furl = classLoader.getResource("config.xml");
		try {
			writeProfiles();
			writeAdapters();
			writeUsers();
			writeMaps();
			writeConfigurations();
		} catch (Exception e) {
			LogManager.getInstance().logMessage(e);
		}
		// remove spaces - if any...
		String configPath = URLDecoder.decode(furl.getPath(), "UTF-8");

		File configFile = new File(configPath);
		File backup = new File(configFile.getPath() + ".bak");
		System.out.println(backup);

		// backup - if poss
		LogMessage msg = new LogMessage(LogMessage.INFO, configFile,
				"Backing up config: " + configFile.getAbsolutePath(),
				"Backing up to: " + backup.getAbsolutePath());
		LogManager.getInstance().logMessage(msg);
		LogMessage msg2 = new LogMessage(LogMessage.INFO, configFile,
				"Backup deleted: " + backup.delete(), "");
		LogManager.getInstance().logMessage(msg2);
		boolean backedup = FileUtil.copy(configFile.getAbsolutePath(), backup
				.getAbsolutePath());// fconfigFile.renameTo(backup));
		if (!backedup) {
			LogMessage msg3 = new LogMessage(LogMessage.INFO, configFile,
					"Config File not backed up",
					"check file system permissions etc");
			LogManager.getInstance().logMessage(msg3);
		}

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			// Prepare the DOM document for writing
			Source source = new DOMSource(configDoc);

			// Prepare the output file
			Result result = new StreamResult(configFile.getAbsolutePath());

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			System.out.println(source.getSystemId());
			System.out.println(result.getSystemId());
			xformer.transform(source, result);
		} catch (TransformerException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/** The cool stuff that reads the config... * */
	private void readConfig() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			// find the file...
			InputStream cfgFile = null;
			try {
				cfgFile = classLoader.getResourceAsStream("config.xml");
				configDoc = builder.parse(cfgFile);
			}
			catch(Throwable t) { 
				throw new RuntimeException("Config file not found - make sure it is in the classpath", t);				
			}
			finally {
				cfgFile.close();
			}
			//configDoc = builder.parse(configFile);

			// find the <meta-config> tag
			Node metaConfig = null;
			for (int i = 0; i < configDoc.getChildNodes().getLength(); i++) {
				if (ROOT_TAG.equalsIgnoreCase(configDoc.getChildNodes().item(i)
						.getNodeName())) {
					metaConfig = configDoc.getChildNodes().item(i);
				}
			}
			if (metaConfig == null) {
				throw new RuntimeException(
						"Root element <meta-config> not found in config.xml");
			}

			NodeList nodes = metaConfig.getChildNodes();
			boolean app = false, adapters = false, maps = false, configs = false, profiles = false, users = false;
			for (int i = 0; i < nodes.getLength(); i++) {
				if (initApplication(nodes.item(i)))
					app = true;
				if (initAdapters(nodes.item(i)))
					adapters = true;
				if (initMaps(nodes.item(i)))
					maps = true;
				if (initConfigs(nodes.item(i)))
					configs = true;
				if (initProfiles(nodes.item(i)))
					profiles = true;
				if (initUsers(nodes.item(i)))
					users = true;
			}

			if (!app) {
				System.out
						.println("No application configuration information found in config.xml");
			}
			if (!adapters) {
				System.out.println("No Adapters found in config.xml");
			}
			if (!maps) {
				System.out.println("No Maps found in config.xml");
			}
			if (!configs) {
				System.out
						.println("No processing configurations found in config.xml");
			}
			if (!profiles) {
				System.out.println("No profiles found in config.xml");
			}
			if (!users) {
				System.out.println("Incorrect users tag in config.xml");
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}

	private boolean initApplication(Node node) {
		boolean foundInit = false;
		if (HARVESTER_TAG.equalsIgnoreCase(node.getNodeName())) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node appParam = nodes.item(i);
				if (TITLE_TAG.equalsIgnoreCase(appParam.getNodeName())) {
					appName = appParam.getAttributes().getNamedItem(NAME_TAG);
				}
				if (INPUT_FILES_TAG.equalsIgnoreCase(appParam.getNodeName())) {
					baseHarvestDir = appParam.getAttributes().getNamedItem(
							DIR_NAME_TAG);
				}
				if (XML_BASE_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					baseXMLDir = appParam.getAttributes().getNamedItem(URL_TAG);
					foundInit = true;
				}
				if (JAR_BASE_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					baseJarDir = appParam.getAttributes().getNamedItem(URL_TAG);
					Loader.setJarDir(baseJarDir.getNodeValue());
				}
				if (ADMIN_PASSWORD_TAG.equalsIgnoreCase(nodes.item(i)
						.getNodeName())) {
					// the value is in the tag - not an attribute...
					adminPasswordNode = appParam.getFirstChild();
					readAdminPassword(appParam.getFirstChild().getNodeValue());
				}
				if (LOG_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					// the value is in the tag - not an attribute...
					logDirectory = appParam.getAttributes().getNamedItem(
							DIR_NAME_TAG);
				}
			}
		}
		return foundInit;
	}

	private void readAdminPassword(String hashedValue) {
		// System.out.println(hashedValue);
		BlowfishEasy bf = new BlowfishEasy(SEED.toCharArray());
		// System.out.println(bf);
		adminPassword = bf.decryptString(hashedValue);// .trim();
		// System.out.println(adminPassword);
	}

	private boolean initAdapters(Node node) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		boolean initAdapter = false;
		if (ADAPTERS_TAG.equalsIgnoreCase(node.getNodeName())) {
			adaptersNode = node;
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (ADAPTER_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					Node adapterNode = nodes.item(i);
					NamedNodeMap map = adapterNode.getAttributes();
					String className = map.getNamedItem(CLASS_TAG)
							.getNodeValue();
					// String outputDTD =
					// map.getNamedItem(OUTPUT_DTD_TAG).getNodeValue();
					// create it and set it up...
					DataAdapter adapter = null;
					try {
						adapter = (DataAdapter) Class.forName(className, true,
								classLoader)
								.newInstance();
						String jarName = map.getNamedItem(JAR_TAG)
								.getNodeValue();
						setJarForAdapter(adapter, jarName);
					} catch (ClassCastException e) {
						LogManager
								.getInstance()
								.logMessage(
										new LogMessage(
												LogMessage.ERROR,
												e,
												"Adapter class " + className
														+ " not found",
												"Invalid adapter entry in config.xml file - check contents using admin tool or filesystem"));
					} catch (Exception e) {
						LogManager
								.getInstance()
								.logMessage(
										new LogMessage(
												LogMessage.ERROR,
												e,
												"Adapter class " + className
														+ " not found",
												"Invalid adapter entry in config.xml file - check contents using admin tool or filesystem"));
					}

					// are there any properties for the adapter.
					NodeList params = adapterNode.getChildNodes();
					for (int p = 0; p < params.getLength(); p++) {
						if (PARAMETER_TAG.equalsIgnoreCase(params.item(p)
								.getNodeName())) {
							NamedNodeMap paramMap = params.item(p)
									.getAttributes();
							String name = paramMap.getNamedItem(NAME_TAG)
									.getNodeValue();
							String value = paramMap.getNamedItem(VALUE_TAG)
									.getNodeValue();
							try {
								FXUtil.setProperty(adapter, name, value);
							} catch (Exception ex) {
								throw new RuntimeException(
										"Invalid Adapter parameter");
							}
						}
					}

					// tell the directory about the adapter...
					if (adapter != null) {
						AdapterFactory.getInstance().addAdapter(adapter);
						initAdapter = true;
					}
				}
			}
		}
		return initAdapter;
	}

	private boolean initProfiles(Node node) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		boolean initProfiles = false;
		if (PROFILES_TAG.equalsIgnoreCase(node.getNodeName())) {
			profileNode = node;
			profiles = new ArrayList();
			NamedNodeMap nodeMap = node.getAttributes();
			String defaultProfileName = nodeMap.getNamedItem(
					PROFILE_DEFAULT_NAME_TAG).getNodeValue();
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (PROFILE_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					Profile prof = new Profile();
					Node profileNode = nodes.item(i);
					NamedNodeMap map = profileNode.getAttributes();
					String profileName = map.getNamedItem(NAME_TAG)
							.getNodeValue();
					prof.setName(profileName);
					if (profileName.equals(defaultProfileName)) {
						defaultProfile = prof;
						currentProfile = prof;
					}
					NodeList profileNodes = profileNode.getChildNodes();
					for (int elem = 0; elem < profileNodes.getLength(); elem++) {
						Node profileElement = profileNodes.item(elem);
						if (Config.LOG_TAG.equalsIgnoreCase(profileElement
								.getNodeName())) {
							prof.setLogDirectory(profileElement.getAttributes()
									.getNamedItem(DIR_NAME_TAG).getNodeValue());
						} else if (Config.INPUT_FILES_TAG
								.equalsIgnoreCase(profileElement.getNodeName())) {
							prof.setInputDirectory(profileElement
									.getAttributes().getNamedItem(DIR_NAME_TAG)
									.getNodeValue());
						} else if (Config.ADAPTER_TAG
								.equalsIgnoreCase(profileElement.getNodeName())) {
							NamedNodeMap adapterMap = profileElement
									.getAttributes();
							String adapterClass = adapterMap.getNamedItem(
									CLASS_TAG).getNodeValue();
							prof.setAdapter(adapterClass, true);
						}
					}
					profiles.add(prof);
					initProfiles = true;
				}
			}
		}
		return initProfiles;
	}

	public void removeUser(User user) {
		users.remove(user);
		fireUserEvent(USER_REMOVED, user);
	}

	public User addUser(User user) {
		users.add(user);
		fireUserEvent(USER_ADDED, user);
		return user;
	}

	public void addProfile(Profile p) {
		if (!profiles.contains(p)) {
			profiles.add(p);
			fireProfileEvent(PROFILE_ADDED, p);
		}
	}

	public boolean removeProfile(Profile p) {
		if (profiles.size() > 1) {
			if (profiles.contains(p)) {
				profiles.remove(p);
				if (p.equals(defaultProfile)) {
					defaultProfile = (Profile) profiles.get(0);
				}
				if (p.equals(currentProfile)) {
					currentProfile = (Profile) profiles.get(0);
				}
				fireProfileEvent(PROFILE_REMOVED, p);
				return true;
			}
		}
		return false;
	}

	private void fireUserEvent(int type, User user) {
		Iterator it = userListeners.iterator();
		while (it.hasNext()) {
			UserListener ul = (UserListener) it.next();
			switch (type) {
			case USER_ADDED:
				ul.userAdded(user);
				break;
			case USER_REMOVED:
				ul.userRemoved(user);
				break;
			}
		}
	}

	private void fireProfileEvent(int type, Profile p) {
		Iterator it = profileListeners.iterator();
		while (it.hasNext()) {
			ProfileListener pl = (ProfileListener) it.next();
			switch (type) {
			case PROFILE_ADDED:
				pl.profileAdded(p);
				break;
			case PROFILE_REMOVED:
				pl.profileRemoved(p);
				break;
			case PROFILE_CHANGED:
				pl.profileChanged(p);
				break;
			}
		}
	}

	private boolean initUsers(Node node) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		boolean result = false;
		if (USERS_TAG.equalsIgnoreCase(node.getNodeName())) {
			String defaultUserName = node.getAttributes().getNamedItem(
					USER_DEFAULT_NAME_TAG).getNodeValue();
			if (defaultUserName != null) {
				result = true;
			}
			NodeList nodes = node.getChildNodes();
			usersNode = node;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node appParam = nodes.item(i);
				if (USER_TAG.equalsIgnoreCase(appParam.getNodeName())) {
					Node name = appParam.getAttributes().getNamedItem(
							USER_NAME_TAG);
					User u = new User(name.getNodeValue());
					users.add(u);
					if (u.getName().equals(defaultUserName)) {
						defaultUser = u;
					}
				}
			}
		}
		return result;
	}

	private boolean initMaps(Node node) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		boolean initMaps = false;
		if (MAPS_TAG.equalsIgnoreCase(node.getNodeName())) {
			mapNode = node;
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (MAP_TAG.equalsIgnoreCase(nodes.item(i).getNodeName())) {
					Node mapNode = nodes.item(i);
					NodeList map = mapNode.getChildNodes();
					String inDTD = null;
					String outDTD = null;
					String xsltDoc = null;
					for (int elem = 0; elem < map.getLength(); elem++) {
						Node mapElement = map.item(elem);
						if (INPUT_DTD_TAG.equalsIgnoreCase(mapElement
								.getNodeName())) {
							inDTD = mapElement.getAttributes().getNamedItem(
									DOC_NAME_TAG).getNodeValue();
						}
						if (OUTPUT_DTD_TAG.equalsIgnoreCase(mapElement
								.getNodeName())) {
							outDTD = mapElement.getAttributes().getNamedItem(
									DOC_NAME_TAG).getNodeValue();
						}
						if (XSLT_TAG.equalsIgnoreCase(mapElement.getNodeName())) {
							xsltDoc = mapElement.getAttributes().getNamedItem(
									DOC_NAME_TAG).getNodeValue();
						}
					}

					// set up the mappings...
					addMapping(inDTD, outDTD, xsltDoc);
					initMaps = true;
				}
			}
		}
		return initMaps;
	}

	private boolean initConfigs(Node node) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		boolean initConfigs = false;
		if (CONFIGURATIONS_TAG.equalsIgnoreCase(node.getNodeName())) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (CONFIGURATION_TAG.equalsIgnoreCase(nodes.item(i)
						.getNodeName())) {
					configNode = node;
					Node mapNode = nodes.item(i);
					NodeList map = mapNode.getChildNodes();
					String name = mapNode.getAttributes()
							.getNamedItem(NAME_TAG).getNodeValue();
					String outputDir = null;
					String outputDTD = null;
					String className = null;
					for (int elem = 0; elem < map.getLength(); elem++) {
						Node mapElement = map.item(elem);
						if (OUTPUT_DIRECTORY_TAG.equalsIgnoreCase(mapElement
								.getNodeName())) {
							outputDir = mapElement.getAttributes()
									.getNamedItem(DIR_NAME_TAG).getNodeValue();
						}
						if (OUTPUT_DTD_TAG.equalsIgnoreCase(mapElement
								.getNodeName())) {
							// optional...
							outputDTD = mapElement.getAttributes()
									.getNamedItem(DOC_NAME_TAG).getNodeValue();
						}
						if (HARVESTER_TAG.equalsIgnoreCase(mapElement
								.getNodeName())) {
							// optional...
							className = mapElement.getAttributes()
									.getNamedItem(CLASS_TAG).getNodeValue();
						}
					}

					// set up the mappings...
					Configuration config = new Configuration(name, className,
							outputDir, outputDTD);
					addConfig(config);
					initConfigs = true;
				}
			}
		}
		return initConfigs;
	}

	private boolean writeUsers() {
		while (usersNode.hasChildNodes()) {
			usersNode.removeChild(usersNode.getFirstChild());
		}
		Element userEl = (Element) usersNode;
		userEl.setAttribute("default", getDefaultUser().getName());
		Iterator it = users.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			Element userNode = configDoc.createElement("user");
			userNode.setAttribute("name", user.getName());
			usersNode.appendChild(userNode);
		}
		return true;
	}

	private boolean writeProfiles() {
		while (profileNode.hasChildNodes()) {
			profileNode.removeChild(profileNode.getFirstChild());
		}
		Iterator it = profiles.iterator();
		while (it.hasNext()) {
			Profile prof = (Profile) it.next();
			Element profNode = configDoc.createElement("profile");
			profNode.setAttribute("name", prof.getName());
			Element inpDirNode = configDoc.createElement(INPUT_FILES_TAG);
			inpDirNode.setAttribute(DIR_NAME_TAG, prof.getInputDirectory());
			Element logDirNode = configDoc.createElement(LOG_TAG);
			logDirNode.setAttribute(DIR_NAME_TAG, prof.getLogDirectory());
			profNode.appendChild(inpDirNode);
			profNode.appendChild(logDirNode);
			Iterator ad = prof.getAdapterClasses();
			while (ad.hasNext()) {
				String adClass = (String) ad.next();
				Element adapNode = configDoc.createElement(ADAPTER_TAG);
				adapNode.setAttribute(CLASS_TAG, adClass);
				profNode.appendChild(adapNode);
			}
			profileNode.appendChild(profNode);
		}
		return true;
	}

	private boolean writeMaps() {
		while (mapNode.hasChildNodes()) {
			mapNode.removeChild(mapNode.getFirstChild());
		}
		Iterator it = this.configMapping.iterator();
		while (it.hasNext()) {
			ConfigMapEntry map = (ConfigMapEntry) it.next();
			Element mapSubNode = configDoc.createElement("map");
			Element inpDTDNode = configDoc.createElement(INPUT_DTD_TAG);
			inpDTDNode.setAttribute(DOC_NAME_TAG, map.getInputDTD());
			Element outDTDNode = configDoc.createElement(OUTPUT_DTD_TAG);
			outDTDNode.setAttribute(DOC_NAME_TAG, map.getOutputDTD());
			Element mapXSLTNode = configDoc.createElement(XSLT_TAG);
			mapXSLTNode.setAttribute(DOC_NAME_TAG, map.getXsltFunction());
			mapSubNode.appendChild(inpDTDNode);
			mapSubNode.appendChild(outDTDNode);
			mapSubNode.appendChild(mapXSLTNode);
			mapNode.appendChild(mapSubNode);
		}
		return true;
	}

	private boolean writeAdapters() {
		while (adaptersNode.hasChildNodes()) {
			adaptersNode.removeChild(adaptersNode.getFirstChild());
		}
		DataAdapter[] ads = AdapterFactory.getInstance().getAdapters();
		for (int i = 0; i < ads.length; i++) {
			Element adSubNode = configDoc.createElement(ADAPTER_TAG);
			adSubNode.setAttribute(CLASS_TAG, ads[i].getClass().getName());
			String ot = ads[i].getOutputType();
			if ((ot != null) && (ot.length() > 0)) {
				adSubNode.setAttribute(OUTPUT_DTD_TAG, ads[i].getOutputType());
			}
			String jar = getJarForAdapter(ads[i]);
			if ((jar != null) && (jar.length() > 0)) {
				adSubNode.setAttribute(JAR_TAG, getJarForAdapter(ads[i]));
			}
			Element adParamNode = configDoc.createElement(PARAMETER_TAG);
			// FXUtil.get
			// ads[i].
			// adParamNode.
			adaptersNode.appendChild(adSubNode);
		}
		return true;
	}

	private boolean writeConfigurations() {
		while (configNode.hasChildNodes()) {
			configNode.removeChild(configNode.getFirstChild());
		}
		Iterator it = this.configs.iterator();
		while (it.hasNext()) {
			Configuration config = (Configuration) it.next();
			Element configSubNode = configDoc.createElement("configuration");
			configSubNode.setAttribute(NAME_TAG, config.getName());
			Element outputDirNode = configDoc
					.createElement(OUTPUT_DIRECTORY_TAG);
			outputDirNode.setAttribute(DIR_NAME_TAG, config
					.getOutputDirectory());
			configSubNode.appendChild(outputDirNode);
			Element harvesterNode = configDoc.createElement(HARVESTER_TAG);
			harvesterNode.setAttribute(CLASS_TAG, config.getClassName());
			configSubNode.appendChild(harvesterNode);
			configNode.appendChild(configSubNode);
		}
		return true;
	}

	public void addUserListener(UserListener ul) {
		userListeners.add(ul);
	}

	public void removeUserListener(UserListener ul) {
		userListeners.remove(ul);
	}

	public void addProfileListener(ProfileListener pl) {
		profileListeners.add(pl);
	}

	public void removeProfileListener(ProfileListener pl) {
		profileListeners.remove(pl);
	}

}