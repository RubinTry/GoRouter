package cn.gorouter.gorouter_compiler.processor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.security.Key;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import cn.gorouter.gorouter_annotation.Route;

/**
 * @author logcat
 * @date 2020/07/13
 */
@AutoService(Processor.class)//注册一个注解处理器
public class GoRouterCompiler extends AbstractProcessor {


    private Filer filter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filter = processingEnvironment.getFiler();
    }


    /**
     * 声明注解处理器支持的java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    /**
     * 声明注解处理器要处理的注解是哪些
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Route.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Route.class);
        // TypeElement  类节点 代表一个类
        //ExecutableElement 方法节点
        // VariableElement成员变量节点
        Map<String, String> map = new HashMap<>();
        Map<String , String> typeMap = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            String key = typeElement.getAnnotation(Route.class).value();
            String nodeName = typeElement.getQualifiedName().toString();
            map.put(key, nodeName + ".class");
            //得到当前元素的类型
            String typeName = typeElement.getSuperclass().toString();
            typeMap.put(key , typeName);
        }

        if (map.size() > 0) {
            Writer writer = null;
            //创建一个新类名
            String nodeName = "RouteBinder" + System.currentTimeMillis();
            //生成文件
            try {
                JavaFileObject sourceFile = filter.createSourceFile("cn.gorouter.route." + nodeName);
                writer = sourceFile.openWriter();
                writer.write("package cn.gorouter.route;\n" +
                        "\n" +
                        "import cn.gorouter.gorouter_api.launcher.GoRouter;\n" +
                        "import android.util.Log;\n" +
                        "import cn.gorouter.gorouter_api.launcher.IRouter;\n\n" +
                        "/**\n" +
                        " * @author logcat\n" +
                        " */\n" +
                        "public class " + nodeName + " implements IRouter {\n" +
                        "    @Override\n" +
                        "    public void put() {\n");

                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String nodeNames = map.get(key);
                    writer.write("\n" +
                            "        GoRouter.getInstance().put(\"" + key +
                            "\" , " + nodeNames + " , \"" + typeMap.get(key) +
                            "\");");
                }
                writer.write("\n" +
                        "    }\n" +
                        "}");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}