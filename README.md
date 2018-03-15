[![Build Result](https://api.travis-ci.org/RalleYTN/SimpleRegistry.svg?branch=master)](https://travis-ci.org/RalleYTN/SimpleRegistry)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/80b62e23b20d4f729e13744a4406f847)](https://www.codacy.com/app/ralph.niemitz/SimpleRegistry?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=RalleYTN/SimpleRegistry&amp;utm_campaign=Badge_Grade)

# Description

SimpleRegistry is pure Java library that grants you read and write access to the Windows registry.
This is done by calling the "reg" console commands through Java.
This method is not the fastest but it gets the job done without any DLLs on the library path.
Windows XP and older versions of Windows are not supported by this library.
Tests show that it definitely works from Windows Vista to Windows 10.

### Code example

```java
try {
	
	// Creating Keys
	Registry.setKey(Registry.HKEY_CURRENT_USER + "\\Software\\MyExampleSoftware");
	RegistryKey mySoftwareKey = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software\\MyExampleSoftware");
	mySoftwareKey.setValue("myExampleValue", RegistryValue.Type.REG_SZ, '\0', "Hello World!");
	mySoftwareKey.reload();
	System.out.println(mySoftwareKey.getValueByName("myExampleValue"));

	// Reading keys
	RegstryKey key = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software");
	
	for(RegistryKey child : key.getChilds()) {
	
		System.out.println(child.getName());
	}
	
	// Removing Keys
	Registry.deleteKey(mySoftwareKey.getPath());
	
} catch(IOException exception) {
	
	exception.printStackTrace();
}
```

## Changelog

### Version 2.0.0 (incompatible with older versions of the library)

- Renamed the `Key` and `Value` classes to `RegistryKey` and `RegistryValue`
- Removed the `Reloadable` interface
- Made the project modular for Java 9
- Added Maven support
- Added Unit-Tests
- Added some more documentation
- Added the `getPath()` method to `RegistryValue`
- Fixed a bug that would give a wrong value through the `RegistryValue.getValue()` method if the type is `REG_MULTI_SZ`

### Version 1.1.0

- Added more methods for direct access in the Registry.java
- Added the possibility to remove values (forgot that in the last release)
- Added the possibility to export and import keys
- Added the possibility to reload values

### Version 1.0.0

- Release

## License

```
MIT License

Copyright (c) 2017 Ralph Niemitz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Links

- [Download](https://github.com/RalleYTN/SimpleRegistry/releases)
- [Online Documentation](https://ralleytn.github.io/SimpleRegistry/)
- [Java 8 Compatible Version](https://github.com/RalleYTN/SimpleRegistry/tree/java8)