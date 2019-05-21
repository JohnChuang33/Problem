package com.example.support;

import sun.misc.Unsafe;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

/**
 * @author Haoyang Zhuang
 * @date 2019-05-20 16:52
 **/
class CompilerUtils {
    public static void main(String[] argvs) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        CompilerUtils.loadFromJava(loader, "com.example.CodeGenTest", "" +
                "package com.example;" +
                "import java.util.UUID;" +
                "import com.example.entity.Department;" +
                "public class CodeGenTest {\n" +
                "  public static void main(String[] args) {\n" +
                "    System.out.println(\"Hello World, from a generated program!\");\n" +
                "System.out.println(UUID.randomUUID());" +
                "System.out.println(new Department());" +
                "  }\n" +
                "}\n");
        Class<?> codeGenTest = Class.forName("com.example.CodeGenTest");
        Method main = codeGenTest.getMethod("main", String[].class);
        main.invoke(null, new Object[]{null});
    }

    private static final Method DEFINE_CLASS_METHOD;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);
            DEFINE_CLASS_METHOD = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            Field f = AccessibleObject.class.getDeclaredField("override");
            long offset = u.objectFieldOffset(f);
            u.putBoolean(DEFINE_CLASS_METHOD, offset, true);
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    static void loadFromJava(ClassLoader classLoader1, String className, String javaCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        JavaFileObject compilationUnit =
                new StringSourceFileObject(className, javaCode);
        SimpleJavaFileManager fileManager =
                new SimpleJavaFileManager(compiler.getStandardFileManager(null, null, null));

        JavaCompiler.CompilationTask compilationTask = compiler.getTask(
                null, fileManager, null, null, null, Collections.singletonList(compilationUnit));

        compilationTask.call();

        CompilerUtils.defineClass(classLoader1, className,
                fileManager.getGeneratedOutputFiles().get(0).outputStream.toByteArray());
    }

    private static Class defineClass(ClassLoader classLoader, String className, byte[] bytes) {
        try {
            return (Class) DEFINE_CLASS_METHOD.invoke(classLoader, className, bytes, 0, bytes.length);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new AssertionError(e.getCause());
        }
    }

    static class StringSourceFileObject extends SimpleJavaFileObject {

        /**
         * The source code of this "file".
         */
        private final String code;

        /**
         * Constructs a new StringBuilderJavaSource.
         * @param name the name of the source file represented by this file object
         */
        StringSourceFileObject(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    private static class ClassJavaFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream;
        private final String className;

        ClassJavaFileObject(String className, Kind kind) {
            super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
            this.className = className;
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }

        byte[] getBytes() {
            return outputStream.toByteArray();
        }

        String getClassName() {
            return className;
        }
    }

    private static class SimpleJavaFileManager extends ForwardingJavaFileManager {
        private final List<ClassJavaFileObject> outputFiles;

        SimpleJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
            outputFiles = new ArrayList<>();
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
            outputFiles.add(file);
            return file;
        }

        public List<ClassJavaFileObject> getGeneratedOutputFiles() {
            return outputFiles;
        }
    }
}
