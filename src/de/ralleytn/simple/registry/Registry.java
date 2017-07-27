/*
 * MIT License
 * 
 * Copyright (c) 2017 Ralph Niemitz
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.ralleytn.simple.registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to the Windows registry file.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 1.1.0
 * @since 1.0.0
 */
public final class Registry {

	/** @since 1.0.0 */ public static final String HKEY_CLASSES_ROOT = "HKEY_CLASSES_ROOT";
	/** @since 1.0.0 */ public static final String HKEY_CURRENT_USER = "HKEY_CURRENT_USER";
	/** @since 1.0.0 */ public static final String HKEY_LOCAL_MASHINE = "HKEY_LOCAL_MASHINE";
	/** @since 1.0.0 */ public static final String HKEY_USERS = "HKEY_USERS";
	/** @since 1.0.0 */ public static final String HKEY_CURRENT_CONFIG = "HKEY_CURRENT_CONFIG";
	/** @since 1.0.0 */ public static final String HKEY_DYN_DATA = "HKEY_DYN_DATA";
	
	private Registry() {}
	
	/**
	 * Deletes all values of a registry key.
	 * @param path path of the key
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void deleteAllValues(String path) throws IOException {
		
		Registry.exec("reg delete \"" + path + "\" /va /f");
	}
	
	/**
	 * Deletes the default value of a registry key.
	 * @param path path of the key
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void deleteDefaultValue(String path) throws IOException {
		
		Registry.exec("reg delete \"" + path + "\" /ve /f");
	}
	
	/**
	 * Deletes a value from a registry key.
	 * @param path path of the key
	 * @param name name of the value that should be deleted
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void deleteValue(String path, String name) throws IOException {
		
		Registry.exec("reg delete \"" + path + "\" /v " + name + " /f");
	}
	
	/**
	 * Sets a value of a registry key.
	 * @param path path of the key
	 * @param name name of the value
	 * @param type the data type
	 * @param seperator only important when the data type is {@linkplain Value.Type#REG_MULTI_SZ}; specifies at which character the string should be split; some characters do not work as seperator and simply do nothing like '|' for instance
	 * @param rawValue the value data
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void setValue(String path, String name, Value.Type type, char seperator, String rawValue) throws IOException {
		
		Registry.exec("reg add \"" + path + "\" /v " + name + " /t " + type.name() + (type == Value.Type.REG_MULTI_SZ ? " /s " + seperator : "") + " /d \"" + rawValue + "\" /f");
	}
	
	/**
	 * Sets the default value of a registry key.
	 * @param path path of the key
	 * @param type the data type
	 * @param seperator only important when the data type is {@linkplain Value.Type#REG_MULTI_SZ}; specifies at which character the string should be split; some characters do not work as seperator and simply do nothing like '|' for instance
	 * @param rawValue the value data
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void setDeafultValue(String path, Value.Type type, char seperator, String rawValue) throws IOException {
		
		Registry.exec("reg add \"" + path + "\" /ve /t " + type.name() + (type == Value.Type.REG_MULTI_SZ ? " /s " + seperator : "") + " /d \"" + rawValue + "\" /f");
	}

	/**
	 * @param path path of the key
	 * @param name name of the value
	 * @return a value
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final Value getValue(String path, String name) throws IOException {
		
		String result = Registry.exec("reg query \"" + path + "\" /v " + name);
		
		for(String line : result.split("\n")) {
			
			if(line.startsWith(" >")) {
				
				String[] valueAttribs = line.substring(2).split("\\|");
				return new Value(valueAttribs[0], Value.Type.getTypeByName(valueAttribs[1]), valueAttribs.length == 2 ? null : valueAttribs[2], path);
			}
		}
		
		return null;
	}
	
	/**
	 * Imports keys and values from a file.
	 * @param file file containing the data to import
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void importFile(File file) throws IOException {
		
		Registry.exec("reg import \"" + file.getAbsolutePath() + "\"");
	}
	
	/**
	 * Exports a registry key to a specified file.
	 * @param path the key that should be exported
	 * @param exportFile the export target
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public static final void exportKey(String path, File exportFile) throws IOException {
		
		Registry.exec("reg export \"" + path + "\" \"" + exportFile.getAbsolutePath() + "\" /y");
	}
	
	/**
	 * Deletes a key. It will be deleted without asking!
	 * @param path path of the key
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public static final void deleteKey(String path) throws IOException {
		
		Registry.exec("reg delete \"" + path + "\" /f");
	}
	
	/**
	 * Adds or replaces a key. Keys will be replaced without asking first!
	 * @param path path of the key
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public static final void setKey(String path) throws IOException {
		
		Registry.exec("reg add \"" + path + "\" /f");
	}
	
	/**
	 * @param path path of the key
	 * @return key with the specified path
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public static final Key getKey(String path) throws IOException {
		
		path = path.replace('/', '\\');
		
		if(path.endsWith("\\")) {
			
			path = path.substring(0, path.length() - 1);
		}
		
		String name = null;
		String parent = null;
		
		if(path.contains("\\")) {
			
			String[] parts = path.split("\\\\");
			name = parts[parts.length - 1];
			
			StringBuilder parentPathBuilder = new StringBuilder();
			boolean first = true;
			
			for(int index = 0; index < parts.length - 1; index++) {
				
				if(first) {
					
					first = false;
					
				} else {
					
					parentPathBuilder.append('\\');
				}
				
				parentPathBuilder.append(parts[index]);
				parent = parentPathBuilder.toString();
			}
			
		} else {
			
			name = path;
		}
		
		String result = Registry.exec("reg query \"" + path + "\"");
		
		if(result != null) {
			
			List<String> childs = new ArrayList<>();
			List<Value> values = new ArrayList<>();
			Value defaultValue = null;
			
			for(String line : result.split("\n")) {

				if(line.startsWith(" >")) {
					
					String[] valueAttribs = line.substring(2).split("\\|");
					
					values.add(new Value(valueAttribs[0], Value.Type.getTypeByName(valueAttribs[1]), valueAttribs.length == 2 ? null : valueAttribs[2], path));
					
				} else if(!line.equals(path)){
					
					childs.add(line);
				}
			}
			
			result = Registry.exec("reg query \"" + path + "\" /ve");
				
			for(String line : result.split("\n")) {
					
				if(line.startsWith(" >")) {
						
					String[] valueAttribs = line.substring(2).split("\\|");
					defaultValue = new Value(valueAttribs[0], Value.Type.getTypeByName(valueAttribs[1]), valueAttribs.length == 2 ? null : valueAttribs[2], path);
				}
			}
			
			return new Key(path, name, values, defaultValue, parent, childs);
		}
		
		return null;
	}
	
	private static final String exec(String cmd) throws IOException {
		
		Process process = Runtime.getRuntime().exec("cmd /c " + cmd);
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
			
			String line = null;
			
			while((line = reader.readLine()) != null) {
				
				if(!line.trim().isEmpty()) {
						
					if(first) {
							
						first = false;
							
					} else {
							
						builder.append('\n');
					}
						
					if(line.startsWith("    ")) {
							
						line = " >" + line.substring(4).replace("    ", "|");
					}
						
					builder.append(line);
				}
			}
		}
		
		if(process.exitValue() != 0) {
			
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
				
				throw new IOException(reader.readLine());
			}
		}
		
		return builder.toString();
	}
}
