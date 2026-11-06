package io.github.yijunl.appserver.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WebAppClassLoader extends ClassLoader{
    private final String appClassesDir;

    public WebAppClassLoader(String appClassesDir) {
        super(WebAppClassLoader.class.getClassLoader());
        this.appClassesDir = appClassesDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String filePath = appClassesDir + File.separator + name.replace('.', File.separatorChar) + ".class";
        File classFile = new File(filePath);

        if(!classFile.exists()) {
            throw new ClassNotFoundException("Class not found in app dir: " + name);
        }

        try(FileInputStream in = new FileInputStream(classFile)){
            byte[] bytes = in.readAllBytes();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Failed to read class file", e);
        }
    }
}
