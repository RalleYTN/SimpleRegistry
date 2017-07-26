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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a key in the registry.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Key {

	private List<String> childs;
	private String parent;
	private List<Value> values;
	private Value defaultValue;
	private String name;
	private String path;
	
	Key(String path, String name, List<Value> values, Value defaultValue, String parent, List<String> childs) {
		
		this.path = path;
		this.name = name;
		this.defaultValue = defaultValue;
		this.values = values;
		this.parent = parent;
		this.childs = childs;
	}
	
	/**
	 * Reloads the key. Should be called after setting or deleting values.
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public void reload() throws IOException {
		
		Key key = Registry.getKey(this.path);
		
		this.childs = key.childs;
		this.parent = key.parent;
		this.values = key.values;
		this.defaultValue = key.defaultValue;
		this.name = key.name;
		this.path = key.path;
	}
	
	/**
	 * Sets the default value.
	 * @param type data type of value data
	 * @param seperator only important when the data type is {@linkplain Value.Type#REG_MULTI_SZ}; specifies at which character the string should be split; some characters do not work as seperator and simply do nothing like '|' for instance
	 * @param rawValue the raw value data
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public void setDefaultValue(Value.Type type, char seperator, String rawValue) throws IOException {
		
		Registry.exec("reg add \"" + path + "\" /ve /t " + type.name() + (type == Value.Type.REG_MULTI_SZ ? " /s " + seperator : "") + " /d \"" + rawValue + "\" /f");
	}
	
	/**
	 * Sets a value.
	 * @param name the value name
	 * @param type data type of value data
	 * @param seperator only important when the data type is {@linkplain Value.Type#REG_MULTI_SZ}; specifies at which character the string should be split; some characters do not work as seperator and simply do nothing like '|' for instance
	 * @param rawValue the raw value data
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public void setValue(String name, Value.Type type, char seperator, String rawValue) throws IOException {
		
		Registry.exec("reg add \"" + path + "\" /v " + name + " /t " + type.name() + (type == Value.Type.REG_MULTI_SZ ? " /s " + seperator : "") + " /d \"" + rawValue + "\" /f");
	}
	
	/**
	 * @return the default value
	 * @since 1.0.0
	 */
	public Value getDefaultValue() {
		
		return this.defaultValue;
	}
	
	/**
	 * @return this key's path
	 * @since 1.0.0
	 */
	public String getPath() {
		
		return this.path;
	}
	
	/**
	 * @return the name of this key
	 * @since 1.0.0
	 */
	public String getName() {
		
		return this.name;
	}
	
	/**
	 * @param name name of the child
	 * @return the child with the specified name, or {@code null} if this key has no child with that name; case insensitive
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public Key getChild(String name) throws IOException {

		for(String child : this.childs) {
			
			String[] parts = child.split("\\\\");
			
			if(parts[parts.length - 1].toUpperCase().equals(name.toUpperCase())) {
				
				return Registry.getKey(child);
			}
		}
		
		return null;
	}
	
	/**
	 * @return all childs of this key
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public List<Key> getChilds() throws IOException {
		
		List<Key> list = new ArrayList<Key>();
		
		for(String child : this.childs) {
			
			list.add(Registry.getKey(child));
		}
		
		return list;
	}
	
	/**
	 * @return the parent of this key
	 * @throws IOException if an error occurs
	 * @since 1.0.0
	 */
	public Key getParent() throws IOException {
		
		return Registry.getKey(this.parent);
	}
	
	/**
	 * @return all values of this key
	 * @since 1.0.0
	 */
	public List<Value> getValues() {
		
		return Key.clone(this.values);
	}
	
	/**
	 * @param name value name
	 * @return the value if the specified name, or {@code null} if no value was found; case insensitive
	 * @since 1.0.0
	 */
	public Value getValueByName(String name) {
		
		for(Value value : values) {
			
			if(value.getName().toUpperCase().equals(name.toUpperCase())) {
				
				return value;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("path=").append(this.path).append(';');
		builder.append("parent=").append(this.parent).append(';');
		builder.append("name=").append(this.name).append(';');
		builder.append("defaultValue=").append(this.defaultValue).append(';');
		builder.append("values=[");
		boolean first = true;
		
		for(Value value : this.values) {
			
			if(first) {
				
				first = false;
				
			} else {
				
				builder.append(',');
			}
			
			builder.append(value.toString());
		}
		
		builder.append("];childs=[");
		first = true;
		
		for(String child : this.childs) {
			
			if(first) {
				
				first = false;
				
			} else {
				
				builder.append(',');
			}
			
			builder.append(child.toString());
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	private static final <T>List<T> clone(List<T> list) {
		
		try {
			
			@SuppressWarnings("unchecked")
			List<T> clonedList = list.getClass().newInstance();
			
			for(T element : list) {
				
				clonedList.add(element);
			}
			
			return clonedList;
			
		} catch(Exception exception) {
			
			exception.printStackTrace();
		}
		
		return null;
	}
}
