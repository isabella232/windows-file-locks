There are 4 tuneables in `Main.java` 

 * `READ_RESOURCE` : to perform a `ClassLoader.getResource()` lookup and read
 * `CLOSE_READ_RESOURCE` : to close the `InputStream` from the `ClassLoader.getResource()` 
 * `CLOSE_READ_FILE` : to close the `JarFile` from the `ClassLoader.getResource()`
 * `CLEAN_CLASSLOADER` : to execute the `ClassLoaderUtils` cleanup hacks
 
The following present the results when running on Windows 10 with JDK 8u121 (64 bit)
  
When `CLEAN_CLASSLOADER = false`
--------------------------------

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is false.

```
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
File was deleted: target\lib\dummy.jar
File not present: target\lib\dummy.jar
```

`READ_RESOURCE` is true. `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is false.

```
Readme read 175 bytes
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:99)
	at demo.Main.main(Main.java:29)
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:107)
	at demo.Main.main(Main.java:29)
FAIL: Unable to delete target\lib\dummy.jar
```

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` is true. `CLOSE_READ_FILE` is false.

```
Readme read 175 bytes
Closing read resource
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:99)
	at demo.Main.main(Main.java:29)
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:107)
	at demo.Main.main(Main.java:29)
FAIL: Unable to delete target\lib\dummy.jar
```

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is true.

```
Readme read 175 bytes
Closing read resource
Closing read jarfile
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
File was deleted: target\lib\dummy.jar
File not present: target\lib\dummy.jar
```

When `CLEAN_CLASSLOADER = true`
--------------------------------

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is false.

```
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
File was deleted: target\lib\dummy.jar
File not present: target\lib\dummy.jar
```

`READ_RESOURCE` is true. `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is false.

```
Readme read 175 bytes
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:99)
	at demo.Main.main(Main.java:29)
Closing: sun.net.www.protocol.jar.URLJarFile@66d3c617
Closing: sun.net.www.protocol.jar.URLJarFile@66d3c617
File was deleted: target\lib\dummy.jar
```

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` is true. `CLOSE_READ_FILE` is false.

```
Readme read 175 bytes
Closing read resource
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
java.nio.file.FileSystemException: target\lib\dummy.jar: The process cannot access the file because it is being used by another process.

	at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:86)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
	at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)
	at sun.nio.fs.WindowsFileSystemProvider.implDelete(WindowsFileSystemProvider.java:269)
	at sun.nio.fs.AbstractFileSystemProvider.delete(AbstractFileSystemProvider.java:103)
	at java.nio.file.Files.delete(Files.java:1126)
	at demo.Main.attemptDelete(Main.java:149)
	at demo.Main.run(Main.java:99)
	at demo.Main.main(Main.java:29)
Closing: sun.net.www.protocol.jar.URLJarFile@66d3c617
Closing: sun.net.www.protocol.jar.URLJarFile@66d3c617
File was deleted: target\lib\dummy.jar
```

`READ_RESOURCE` & `CLOSE_READ_RESOURCE` & `CLOSE_READ_FILE` is true.

```
Readme read 175 bytes
Closing read resource
Closing read jarfile
Closed classloader: clazz=class org.mortbay.jetty.testcase.verifier.DummyModernLib
File was deleted: target\lib\dummy.jar
File not present: target\lib\dummy.jar
```

