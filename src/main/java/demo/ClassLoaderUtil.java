package demo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class ClassLoaderUtil
{
    public static List<JarFile> getOpenJarFiles(URLClassLoader loader)
    {
        Field f = getClassField(URLClassLoader.class, "ucp");
        if (f == null)
        {
            return null;
        }
        
        try
        {
            Object obj = f.get(loader);
            final Object ucp = obj;
            f = getClassField(ucp.getClass(), "loaders");
            if (f == null)
            {
                return null;
            }
            
            ArrayList loaders = (ArrayList) f.get(ucp);
            if (loaders == null)
            {
                return null;
            }
            
            List<JarFile> ret = new ArrayList<>();
            for (int i = 0; i < loaders.size(); i++)
            {
                obj = loaders.get(i);
                f = getClassField(obj.getClass(), "jar"); // from sun.misc.URLClassPath$JarLoader
                if (f == null)
                {
                    f = getClassField(obj.getClass(), "jarfile"); // from sun.misc.URLClassPath$Loader
                }
                
                if (f != null)
                {
                    Object jarObj = f.get(obj);
                    if (jarObj instanceof JarFile)
                    {
                        ret.add((JarFile) jarObj);
                    }
                }
            }
            return ret;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    public static void clearJarFactoryCache(List<JarFile> jarFiles)
    {
        if (jarFiles == null || jarFiles.isEmpty())
        {
            return;
        }
        
        
        Class classJarFileFactory = null;
        try
        {
            classJarFileFactory = Class.forName("sun.net.www.protocol.jar.JarFileFactory");
        }
        catch (ClassNotFoundException ex)
        {
            return;
        }
        
        try
        {
            Field fileCacheField = getClassField(classJarFileFactory, "fileCache");
            Field urlCacheField = getClassField(classJarFileFactory, "urlCache");
            
            Map<String, JarFile> fileCache = (Map<String, JarFile>) fileCacheField.get(null);
            Map<JarFile, URL> urlCache = (Map<JarFile, URL>) urlCacheField.get(null);
            
            List<JarFile> closeableJarFiles = new ArrayList<>();
            for (JarFile closeRef : jarFiles)
            {
                Path closeRefPath = new File(closeRef.getName()).toPath();
                
                closeableJarFiles.clear();
                
                urlCache.entrySet().removeIf((entry) ->
                        isMatch(closeableJarFiles, closeRefPath, entry.getKey()));
                fileCache.entrySet().removeIf((entry) ->
                        isMatch(closeableJarFiles, closeRefPath, entry.getValue()));
                
                // We might wind up with multiple entries for the same JVM
                // internal JarFile implementation/instance, but that's no big deal.
                closeableJarFiles.stream().forEach((jar) -> close(jar));
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace(System.err);
        }
    }
    
    private static boolean isMatch(Collection<JarFile> closeableJarFiles, Path closeRefPath, JarFile jarFile)
    {
        Path cacheRefPath = new File(jarFile.getName()).toPath();
        if (isSameFile(cacheRefPath, closeRefPath))
        {
            // postpone close
            closeableJarFiles.add(jarFile);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static void close(Closeable closeable)
    {
        try
        {
            System.err.printf("Closing: %s%n", closeable);
            closeable.close();
        }
        catch (IOException ignore)
        {
        }
    }
    
    private static boolean isSameFile(Path path1, Path path2)
    {
        try
        {
            return Files.isSameFile(path1, path2);
        }
        catch (IOException e)
        {
            return false;
        }
    }
    
    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... types)
    {
        try
        {
            Method method = clazz.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method;
        }
        catch (NoSuchMethodException e)
        {
            System.err.printf("Unable to find method [%s] in class [%s]%n", methodName, clazz.getName());
            return null;
        }
    }
    
    private static Field getClassField(Class clz, String fieldName)
    {
        Field field = null;
        try
        {
            field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (Exception e)
        {
            System.err.printf("Unable to find field [%s] on class [%s]%n", fieldName, clz.getName());
        }
        return field;
    }
    
}
