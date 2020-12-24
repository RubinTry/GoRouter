package cn.gorouter.compiler.processor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
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
import cn.gorouter.annotation.Route;


/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.0
 * @since 2020/07/11 16:25
 */
@AutoService(Processor.class)//Register a annotation processor
public class GoRouterCompiler extends AbstractProcessor {


    private Filer filter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filter = processingEnvironment.getFiler();
    }


    /**
     * Declare the Java version supported by the annotation processor
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    /**
     * What are the annotations to be processed by the annotation processor
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
        initRoute(roundEnvironment);
        return false;
    }



    private void initRoute(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Route.class);
        // TypeElement  类节点 代表一个类
        //ExecutableElement 方法节点
        // VariableElement成员变量节点
        Map<String, String> map = new HashMap<>();
//        Map<String, String> typeMap = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            String url = typeElement.getAnnotation(Route.class).url();
            String nodeName = typeElement.getQualifiedName().toString();
            map.put(url, nodeName + ".class");
        }

        if (map.size() > 0) {
            Writer writer = null;
            //Create a new className.
            String nodeName = "RouteBinder" + System.currentTimeMillis() + "Impl";
            //Generate a file.
            try {
                JavaFileObject sourceFile = filter.createSourceFile("cn.gorouter.route." + nodeName);
                writer = sourceFile.openWriter();
                writer.write("package cn.gorouter.route;\n" +
                        "\n" +
                        "import cn.gorouter.api.launcher._GoRouter;\n" +
                        "import cn.gorouter.api.launcher.IRouter;\n\n" +
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
                            "        _GoRouter.getInstance().put(\"" + key +
                            "\" , " + nodeNames + ");");
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
    }


}