# Description
SimpleRegistry is pure Java library that grants you read and write access to the Windows registry.

# Requirements
- Java 8 or later
- Windows Vista or later (XP and older systems are not supported)

# Setup
Just put the Java archive on your build path.

# Example
```java
public static void main(String[] args) {

	try {
	
		// Creating Keys
		Registry.setKey(Registry.HKEY_CURRENT_USER + "\\Software\\MyExampleSoftware");
		Key mySoftwareKey = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software\\MyExampleSoftware");
		mySoftwareKey.setValue("myExampleValue", Value.Type.REG_SZ, '\0', "Hello World!");
		mySoftwareKey.reload();
		System.out.println(mySoftwareKey.getValueByName("myExampleValue"));

		// Reading keys
		Key key = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software");
	
		for(Key child : key.getChilds()) {
	
			System.out.println(child.getName());
		}
	
		// Removing Keys
		Registry.deleteKey(mySoftwareKey.getPath());
	
	} catch(IOException exception) {
	
		exception.printStackTrace();
	}
}
```

# Changelog

## Version 1.1.0

	- Added more methods for direct access in the Registry.java
	- Added the possibility to remove values (forgot that in the last release)
	- Added the possibility to export and import keys
	- Added the possibility to reload values

## Version 1.0.0

	- Release

# Links
See the [download page](https://github.com/RalleYTN/SimpleRegistry/releases)    
See the [changelog](https://github.com/RalleYTN/SimpleRegistry/blob/master/CHANGELOG.md)    
See the [online documentation](https://ralleytn.github.io/SimpleRegistry/)