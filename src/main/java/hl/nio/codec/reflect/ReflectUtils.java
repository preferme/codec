package hl.nio.codec.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ReflectUtils {

    private ReflectUtils(){}

    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);
    private static final String PACKAGE_INFO_CLASS = "package-info.class";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';

    public static ClassLoader defaultClassLoader(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ReflectUtils.class.getClassLoader();
        }
        return loader;
    }

    public static Set<Class<?>> loadClasses(String packageName, ClassLoader cl, boolean recursive, Predicate<Class<?>> filter) throws IOException, ClassNotFoundException {
        LinkedHashSet<Class<?>> results = new LinkedHashSet<>();
        String pathName =  packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
        Enumeration<URL> urls =  cl.getResources(pathName);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("[LoaderUtils][loadClasses] load class from => {}", url);
            }

            String protocol = url.getProtocol();

            switch (protocol) {
                case "file" :
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    loadFileClasses(results, filePath, packageName, cl, recursive, filter);
                    break;
                case "jar" :
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    loadJarFileClasses(packageName, cl, recursive, results, pathName, jar, filter);
                    break;
                default:
                    log.warn("[LoaderUtils][loadClasses] not support to load {}", url);
            }

        }
        return results;
    }

    private static void loadJarFileClasses(String packageName, ClassLoader cl, boolean recursive, LinkedHashSet<Class<?>> results, String pathName, JarFile jar, Predicate<Class<?>> filter) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jar.entries();
        String packName = packageName;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            name = name.charAt(0) == '/' ? name.substring(1) : name;
            if (name.startsWith(pathName)) {
                int idx = name.lastIndexOf(PATH_SEPARATOR);
                if (idx >= 0) {
                    packName = name.substring(0, idx).replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
                }
                if ( (idx>=0) || recursive) {
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        String className = packName + "." +  name.substring(packName.length()+1, name.length()-6);
                        if (log.isDebugEnabled()) {
                            log.debug("[LoaderUtils][loadJarFileClasses] load class => {}", className);
                        }
                        Class<?> klass = cl.loadClass(className);
                        if (filter.test(klass)) {
                            results.add(klass);
                        }
                    }
                }
            }
        }
    }

    private static void loadFileClasses(LinkedHashSet<Class<?>> results, String filePath, String packageName, ClassLoader cl, boolean recursive, Predicate<Class<?>> filter) throws ClassNotFoundException {
        File path = new File(filePath);
        if (!path.exists() || !path.isDirectory()) {
            return;
        }
        File[] children = path.listFiles(file->(recursive && file.isDirectory()) || (file.isFile() && file.getName().endsWith(".class")));
        for (File file : children) {
            if (file.isDirectory()) {
                loadFileClasses(results, file.getPath(), packageName + "." +file.getName(), cl, recursive, filter);
            } else {
                if (file.getName().equals(PACKAGE_INFO_CLASS)) {
                    continue;
                }
                String className = packageName + '.' + file.getName().substring(0, file.getName().length()-6);
                if (log.isDebugEnabled()) {
                    log.debug("[LoaderUtils][loadFileClasses] load class => {}", className);
                }
                Class<?> klass = cl.loadClass(className);
                if (filter.test(klass)) {
                    results.add(klass);
                }
            }

        }
    }

}
