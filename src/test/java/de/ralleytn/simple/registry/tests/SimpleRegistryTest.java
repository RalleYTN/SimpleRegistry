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
package de.ralleytn.simple.registry.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import de.ralleytn.simple.registry.RegistryKey;
import de.ralleytn.simple.registry.RegistryValue;
import de.ralleytn.simple.registry.Registry;

/**
 * Tests the {@linkplain Registry} class.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 2.0.0
 * @since 2.0.0
 */
class SimpleRegistryTest {

	private static final String KEY_NAME = "MyExampleSoftware";
	private static final String KEY = Registry.HKEY_CURRENT_USER + "\\Software\\" + KEY_NAME;
	private static final String CHILD1 = KEY + "\\Child1";
	private static final String CHILD2 = KEY + "\\Child2";
	private static final String CHILD3 = KEY + "\\Child3";
	private static final File FILE = new File("myKey.reg");
	
	private static final void setKey(String path) throws IOException {
		
		// DO
		Registry.setKey(path);
		RegistryKey key = Registry.getKey(path);
		
		// TEST RESULT
		assertNotNull(key);
		assertEquals(path, key.getPath());
		assertEquals(path.substring(path.lastIndexOf('\\') + 1), key.getName());
	}
	
	private static final void checkValue(RegistryValue value, String expectedRawValue, Object expectedValue, RegistryValue.Type expectedType, String expectedPath, String expectedName) throws IOException {
		
		assertNotNull(value);
		assertEquals(expectedRawValue, value.getRawValue());
		assertEquals(expectedValue, value.getValue());
		assertEquals(expectedType, value.getType());
		assertEquals(expectedPath, value.getPath());
		assertEquals(expectedName, value.getName());
	}
	
	private static final void checkIfExists(String path) {
		
		boolean correct = false;
		
		try {
			
			Registry.getKey(path);
			
		} catch(IOException exception) {
			
			correct = true;
		}
		
		assertTrue(correct, "The key " + path + " still exists!");
	}
	
	/**
	 * Cleans up potential leftovers from tests.
	 * @since 2.0.0
	 */
	@AfterAll
	static void cleanUp() {
		
		try {
			
			Registry.deleteKey(KEY);
			
		} catch(IOException exception) {}
		
		FILE.delete();
	}
	
	@Test
	void testDeleteKey() {
		
		// Test with the static methods of the Registry class
		try {
			
			// SETUP
			setKey(KEY);
			setKey(CHILD1);
			setKey(CHILD2);
			setKey(CHILD3);
			
			// TEST
			Registry.deleteKey(CHILD3);
			checkIfExists(CHILD3);
			
			// TEST IF SUBKEYS ALSO GET DELETED
			// EXPECTED: YES
			Registry.deleteKey(KEY);
			checkIfExists(KEY);
			checkIfExists(CHILD1);
			checkIfExists(CHILD2);
			
		} catch(IOException exception) {
			
			fail(exception.getClass().getName() + ": " + exception.getMessage());
		}
		
		// ==========
		
		// Test with the methods of the RegistryKey class
		try {
			
			// SETUP
			setKey(KEY);
			setKey(CHILD1);
			setKey(CHILD2);
			setKey(CHILD3);
			
			// TEST
			RegistryKey key = Registry.getKey(CHILD3);
			key.delete();
			checkIfExists(CHILD3);
			
			// TEST IF SUBKEYS ALSO GET DELETED
			// EXPECTED: YES
			key = Registry.getKey(KEY);
			key.delete();
			checkIfExists(KEY);
			checkIfExists(CHILD1);
			checkIfExists(CHILD2);
			
		} catch(IOException exception) {
			
			fail(exception.getClass().getName() + ": " + exception.getMessage());
		}
	}

	/**
	 * Tests the {@link Registry#setKey(String)} method.
	 * @since 2.0.0
	 */
	@Test
	void testSetKey() {
		
		try {
			
			// CLEANUP
			try {
				
				Registry.getKey(KEY);
				System.err.println("WARNING! testSetKey: The example registry key already exists! Try to delete...");
				Registry.deleteKey(KEY);
				boolean correct = false;
				
				try {
					
					Registry.getKey(KEY);
					
				} catch(IOException exception) {
					
					correct = true;
				}
				
				assertTrue(correct, "The example registry key could not be deleted!");
				
			} catch(IOException exception) {}
			
			// TEST
			setKey(KEY);
			setKey(CHILD1);
			setKey(CHILD2);
			setKey(CHILD3);
			
			// TEST IF KEYS GET OVERWRITTEN
			// EXPECTED: IF A KEY EXISTS IT WILL NOT BE OVERRIDEN!
			Registry.setValue(KEY, "MyValue", RegistryValue.Type.REG_SZ, '\0', "Hello World!");
			setKey(KEY);
			RegistryValue value = Registry.getValue(KEY, "MyValue");
			checkValue(value, "Hello World!", "Hello World!", RegistryValue.Type.REG_SZ, KEY, "MyValue");
			
		} catch(IOException exception) {
			
			fail(exception.getClass().getName() + ": " + exception.getMessage());
		}
	}
}
