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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to the Windows registry file.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 1.0.0
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
					
					values.add(new Value(valueAttribs[0], Value.Type.getTypeByName(valueAttribs[1]), valueAttribs.length == 2 ? null : valueAttribs[2]));
					
				} else if(!line.equals(path)){
					
					childs.add(line);
				}
			}
			
			result = Registry.exec("reg query \"" + path + "\" /ve");
				
			for(String line : result.split("\n")) {
					
				if(line.startsWith(" >")) {
						
					String[] valueAttribs = line.substring(2).split("\\|");
					defaultValue = new Value(valueAttribs[0], Value.Type.getTypeByName(valueAttribs[1]), valueAttribs.length == 2 ? null : valueAttribs[2]);
				}
			}
			
			return new Key(path, name, values, defaultValue, parent, childs);
		}
		
		return null;
	}
	
	static final String exec(String cmd) throws IOException {
		
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
		
		if(process.exitValue() == 1) {
			
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
				
				throw new IOException(reader.readLine());
			}
		}
		
		return builder.toString();
	}
}
