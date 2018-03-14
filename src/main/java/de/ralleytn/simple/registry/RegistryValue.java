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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents a value of a registry key.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 2.0.0
 * @since 1.0.0
 */
public final class RegistryValue {

	private String name;
	private String path;
	private Type type;
	private String rawValue;
	private Object value;
	
	RegistryValue(String name, Type type, String rawValue, String path) {
		
		this.name = name;
		this.type = type;
		this.rawValue = rawValue;
		this.value = type.parseValue(rawValue);
		this.path = path;
	}
	
	/**
	 * Reloads the value.
	 * @throws IOException if an error occurred while reloading the value
	 * @since 1.0.0
	 */
	public final void reload() throws IOException {
		
		RegistryValue value = Registry.getValue(this.path, this.name);
		
		this.name = value.name;
		this.path = value.path;
		this.type = value.type;
		this.rawValue = value.rawValue;
		this.value = value;
	}
	
	/**
	 * @return value name
	 * @since 1.0.0
	 */
	public final String getName() {
		
		return this.name;
	}
	
	/**
	 * @return data type of value
	 * @since 1.0.0
	 */
	public final Type getType() {
		
		return this.type;
	}
	
	/**
	 * In case of {@link RegistryValue.Type#REG_MULTI_SZ} you have to replace the character that you defined as separator with {@code "\\0"}.
	 * @return raw value data
	 * @since 1.0.0
	 */
	public final String getRawValue() {
		
		return this.rawValue;
	}
	
	/**
	 * @return the path of this value
	 * @since 2.0.0
	 */
	public final String getPath() {
		
		return this.path;
	}
	
	/**
	 * @return the registry key to which this value belongs
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public final RegistryKey getKey() throws IOException {
		
		return Registry.getKey(this.path);
	}
	
	/**
	 * <ul>
	 * <li>REG_BINARY = byte[]</li>
	 * <li>REG_DWORD = int</li>
	 * <li>REG_QWORD = long</li>
	 * <li>REG_SZ = String</li>
	 * <li>REG_EXPAND_SZ = String</li>
	 * <li>REG_MULTI_SZ = List&lt;String&gt;</li>
	 * <li>REG_FULL_RESOURCE_DESCRIPTOR = String</li>
	 * <li>REG_NONE = String</li>
	 * </ul>
	 * @return value data
	 * @since 1.0.0
	 */
	public final Object getValue() {
		
		return this.value;
	}
	
	@Override
	public final String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append('|');
		builder.append(this.type.name());
		builder.append('|');
		builder.append(this.rawValue);
		
		return builder.toString();
	}
	
	/**
	 * Represents the data type of a value in a registry key.
	 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
	 * @version 2.0.0
	 * @since 1.0.0
	 */
	public static enum Type {
		
		/**
		 * byte[]
		 * @since 1.0.0
		 */
		REG_BINARY {

			@Override
			protected final Object parseValue(String toParse) {
				
				byte[] value = null;
				
				try(ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					
					for(int index = 0; index < toParse.length(); index += 2) {
						
						buffer.write(Integer.parseInt(toParse.substring(index, index + 2), 16));
					}
					
					value = buffer.toByteArray();
					
				} catch(IOException exception) {
					
					exception.printStackTrace();
				}
				
				return value;
			}
		},
		
		/**
		 * int
		 * @since 1.0.0
		 */
		REG_DWORD {

			@Override
			protected final Object parseValue(String toParse) {
				
				return Integer.parseInt(toParse.substring(2), 16);
			}
		},
		
		/**
		 * long
		 * @since 1.0.0
		 */
		REG_QWORD {

			@Override
			protected final Object parseValue(String toParse) {
				
				return Long.parseLong(toParse.substring(2), 16);
			}
		},
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_SZ,
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_EXPAND_SZ {
			
			@Override
			protected final Object parseValue(String toParse) {
				
				String value = toParse;

				for(Map.Entry<String, String> entry : System.getenv().entrySet()) {
					
					value = Pattern.compile('%' + entry.getKey() + '%', Pattern.CASE_INSENSITIVE).matcher(value).replaceAll(entry.getValue().replace("\\", "\\\\"));
				}
				
				return value;
			}
		},
		
		/**
		 * List&lt;String&gt;
		 * @since 1.0.0
		 */
		REG_MULTI_SZ {
			
			@Override
			protected final Object parseValue(String toParse) {
				
				List<String> value = new ArrayList<>();

				for(String string : toParse.split("\\\\0")) {
					
					if(string.isEmpty()) {
						
						break;
					}
					
					value.add(string);
				}
				
				return value;
			}
		},
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_FULL_RESOURCE_DESCRIPTOR,
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_NONE;
		
		/**
		 * Parses a registry value based on the implementing type and returns the result.
		 * @param toParse the value that should be parsed
		 * @return the result
		 * @since 1.0.0
		 */
		protected Object parseValue(String toParse) {
			
			return toParse;
		}
		
		/**
		 * @param name data type name
		 * @return the data type with the specified name, or {@code null} if no such data type exists
		 * @since 1.0.0
		 */
		public static final Type getTypeByName(String name) {
			
			for(Type type : Type.values()) {
				
				if(type.name().equals(name)) {
					
					return type;
				}
			}
			
			return null;
		}
	}
}
