/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jsettlers.common.utils.OptionableProperties;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ConfigurationPropertiesFile {
	private static final String SETTLERS_FOLDER = "settlers-folder";

	private final File configFile;
	private final Properties properties;

	public ConfigurationPropertiesFile(OptionableProperties options) throws IOException {
		this(options.getConfigFile());
	}

	public ConfigurationPropertiesFile(File file) throws IOException {
		this.configFile = file;
		this.properties = new Properties();

		if (file.exists()) {
			this.properties.load(new FileInputStream(file));
		}
	}

	public String getSettlersFolderValue() {
		return properties.getProperty(SETTLERS_FOLDER);
	}

	public void setSettlersFolder(File newSettlersFolder) throws FileNotFoundException, IOException {
		properties.setProperty(SETTLERS_FOLDER, newSettlersFolder.getAbsolutePath());
		saveConfigFile("Updated settlers-folder with dialog.");
	}

	private void saveConfigFile(String updateMessage) throws IOException, FileNotFoundException {
		properties.store(new FileOutputStream(configFile), updateMessage);
	}
}