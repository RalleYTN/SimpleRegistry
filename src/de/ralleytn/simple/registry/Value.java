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
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Value {

	private final String name;
	private final Type type;
	private final String rawValue;
	private final Object value;
	
	Value(String name, Type type, String rawValue) {
		
		this.name = name;
		this.type = type;
		this.rawValue = rawValue;
		this.value = type.parseValue(rawValue);
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
	 * @return raw value data
	 * @since 1.0.0
	 */
	public final String getRawValue() {
		
		return this.rawValue;
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
	public String toString() {
		
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
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum Type {
		
		/**
		 * byte[]
		 * @since 1.0.0
		 */
		REG_BINARY {

			@Override
			protected Object parseValue(String toParse) {
				
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
			protected Object parseValue(String toParse) {
				
				return Integer.parseInt(toParse.substring(2), 16);
			}
		},
		
		/**
		 * long
		 * @since 1.0.0
		 */
		REG_QWORD {

			@Override
			protected Object parseValue(String toParse) {
				
				return Long.parseLong(toParse.substring(2), 16);
			}
		},
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_SZ {
			
			@Override
			protected Object parseValue(String toParse) {
				
				return toParse;
			}
		},
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_EXPAND_SZ {
			
			@Override
			protected Object parseValue(String toParse) {
				
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
			protected Object parseValue(String toParse) {
				
				List<String> value = new ArrayList<>();
				
				for(String string : toParse.split("\0")) {
					
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
		REG_FULL_RESOURCE_DESCRIPTOR {
			
			@Override
			protected Object parseValue(String toParse) {
				
				return toParse;
			}
		},
		
		/**
		 * String
		 * @since 1.0.0
		 */
		REG_NONE {
			
			@Override
			protected Object parseValue(String toParse) {
				
				return toParse;
			}
		};
		
		protected abstract Object parseValue(String toParse);
		
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
