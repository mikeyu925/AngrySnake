package com.as.botrunningsystem.utils;

import com.itranswarp.compiler.JavaStringCompiler;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 解决 Reflect.create 空指针问题，问题愿意，joor版本问题，该工具类已废弃
 */
public class CompilerUtil {
    public static Supplier<Integer> generateClass(String className, String packageName, String javaCode) throws Exception {
        // 声明包名
        String prefix = String.format("package %s;", packageName);
        // 全类名
        String fullName = String.format("%s.%s", packageName, className);
        // 编译器
        JavaStringCompiler compiler = new JavaStringCompiler();
        // 编译：compiler.compile("Main.java", source)
        Map<String, byte[]> results = compiler.compile(className + ".java", prefix + javaCode);
        // 加载内存中byte到Class<?>对象
        Class<?> clazz = compiler.loadClass(fullName, results);
        // 创建实例
        Supplier<Integer> instance = (Supplier<Integer>)clazz.getDeclaredConstructor().newInstance();

        return instance;
    }
}
