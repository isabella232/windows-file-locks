package demo;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;

public class Main
{
    private final static boolean READ_RESOURCE = true;
    private final static boolean CLOSE_READ_RESOURCE = true;
    private final static boolean CLOSE_READ_FILE = true;
    private final static boolean CLEAN_CLASSLOADER = true;
    
    public static void main(String[] args)
    {
        try
        {
            new Main().run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    private void run() throws IOException, ClassNotFoundException, InterruptedException
    {
        // Copy jars into temp directory
        Path srcJar = Paths.get("lib", "dummy.jar");
        Path destJar = Paths.get("target", "lib", "dummy.jar");
        
        Path destDir = destJar.getParent();
        if (!Files.exists(destDir))
        {
            Files.createDirectories(destDir);
        }
        
        if (!Files.exists(destJar))
        {
            Files.copy(srcJar, destJar);
        }
        
        Class<?> clazz = null;
        
        URL urls[] = new URL[]{destJar.toUri().toURL()};
        URLClassLoader loader = new URLClassLoader(urls);
        try
        {
            clazz = loader.loadClass("org.mortbay.jetty.testcase.verifier.DummyModernLib");
            
            if (READ_RESOURCE)
            {
                URL url = loader.getResource("README.txt");
                JarURLConnection connection = null;
                try
                {
                    connection = (JarURLConnection) url.openConnection();
                    connection.setUseCaches(true);
                    
                    InputStream in = url.openStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    copy(in, out);
                    
                    String readmeContents = new String(out.toByteArray(), StandardCharsets.UTF_8);
                    System.err.printf("Readme read %,d bytes%n", readmeContents.length());
                }
                finally
                {
                    if (CLOSE_READ_RESOURCE)
                    {
                        System.err.println("Closing read resource");
                        close(connection.getInputStream());
                    }
                    if (CLOSE_READ_FILE)
                    {
                        System.err.println("Closing read jarfile");
                        close(connection.getJarFile());
                    }
                }
            }
        }
        finally
        {
            loader.close();
        }
        
        System.err.println("Closed classloader: clazz=" + clazz);
        attemptDelete(destJar);
        
        if (CLEAN_CLASSLOADER)
        {
            List<JarFile> openJars = ClassLoaderUtil.getOpenJarFiles(loader);
            ClassLoaderUtil.clearJarFactoryCache(openJars);
        }
        
        attemptDelete(destJar);
        
        if (Files.exists(destJar))
        {
            System.err.println("FAIL: Unable to delete " + destJar);
        }
    }
    
    private void copy(InputStream in, OutputStream out) throws IOException
    {
        int bufSize = 512;
        byte buf[] = new byte[bufSize];
        int len;
        while ((len = in.read(buf)) != -1)
        {
            out.write(buf, 0, len);
        }
    }
    
    private void close(Closeable closeable)
    {
        try
        {
            closeable.close();
        }
        catch (IOException e)
        {
            System.err.println("WARNING: Unable to close " + closeable);
        }
    }
    
    private void attemptDelete(Path path)
    {
        if (!Files.exists(path))
        {
            // already deleted, skip
            System.err.println("File not present: " + path);
            return;
        }
        
        try
        {
            Files.delete(path);
            if (!Files.exists(path))
            {
                System.err.println("File was deleted: " + path);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}
