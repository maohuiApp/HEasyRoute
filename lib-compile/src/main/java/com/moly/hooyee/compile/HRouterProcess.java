package com.moly.hooyee.compile;

import com.google.auto.service.AutoService;
import com.moly.hooyee.annocation.Route;
import com.moly.hooyee.model.RouteEntity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)//java版本支持
@SupportedAnnotationTypes({//标注注解处理器支持的注解类型
        "com.moly.hooyee.annocation.Route"
})
public class HRouterProcess extends AbstractProcessor {
    public Filer mFiler; //文件相关的辅助类
    public Elements mElements; //元素相关的辅助类
    public Messager mMessager; //
    public List<RouteEntity> mEntitys;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mEntitys = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        parseRoutes(roundEnv.getElementsAnnotatedWith(Route.class));

        ClassName activity = ClassName.get("com.moly.hooyee.model", "RouteEntity");
        ClassName easyRoute = ClassName.get("com.moly.hooyee.route.api", "EasyRoute");
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteEntity.class)
        );

        MethodSpec.Builder registerBuild = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .addParameter(String.class, "key")
//                .addParameter(activity, "activity")
                .addParameter(inputMapTypeOfGroup, "map");
        for (RouteEntity entity : mEntitys) {
            registerBuild.addStatement("map.put($S, $T.build($S, $S, $S))",
                    entity.getPath(),
                    activity,
                    entity.getClassName(),
                    entity.getPath(),
                    "module"
            );
        }

        MethodSpec register = registerBuild.build();

        CodeBlock.Builder builder = CodeBlock.builder();
        for (RouteEntity e : mEntitys) {
            builder.addStatement("map.put($S, new $T($S, $S))", e.toString(), RouteEntity.class, e.getClassName(), e.getPath());
        }

        String moduleName = "";
        Map<String, String> options = processingEnv.getOptions();
        if (!options.isEmpty()) {
            moduleName = options.get("moduleName");
        }

        TypeSpec route = TypeSpec.classBuilder("EasyRouter$$" + moduleName + "$$RouteManager")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                // static method
//                .addStaticBlock(builder.build())
                .addMethod(register)
                .build();

        try {
            JavaFile javaFile = JavaFile.builder("com.hooyee.easy.route", route)
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

/*    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        parseRoutes(roundEnv.getElementsAnnotatedWith(Route.class));

//        ClassName activity = ClassName.get("android.app", "Activity");
//        ClassName activity = ClassName.get("java.lang", "String");
        ClassName activity = ClassName.get("com.moly.hooyee.model", "RouteEntity");
        ClassName string = ClassName.get("java.lang", "String");
        ClassName map = ClassName.get("java.util", "Map");

        TypeName typeN = ParameterizedTypeName.get(map, string, activity);
        FieldSpec sActivities = FieldSpec.builder(typeN, "sActivities")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", HashMap.class)
                .build();
        MethodSpec register = MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "key")
                .addParameter(activity, "activity")
                .addStatement("$N.put($N, $N)", sActivities, "key", "activity")
                .build();

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "key")
                .addStatement("return $N.get($N)", sActivities, "key")
                .returns(RouteEntity.class)
                .build();

        CodeBlock.Builder builder = CodeBlock.builder();
        for (RouteEntity e : mEntitys) {
            builder.addStatement("register($S, new $T($S, $S))", e.toString(), RouteEntity.class, e.getClassName(), e.getPath());
        }

        String moduleName = "";
        Map<String, String> options = processingEnv.getOptions();
        if (!options.isEmpty()) {
            moduleName = options.get("moduleName");
        }

        TypeSpec route = TypeSpec.classBuilder(moduleName + "$$RouteManager")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(sActivities)
                .addStaticBlock(builder.build())
                .addMethod(register)
                .addMethod(get)
                .build();

        try {
            JavaFile javaFile = JavaFile.builder("com.hooyee.easy.route", route)
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }*/

    private void parseRoutes(Set<? extends Element> annotations) {
//        try {
//            System.setOut(new PrintStream(new FileOutputStream(new File("C:\\t.txt"))));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        for (Element element : annotations) {
            if (element.getKind().isClass()) {
                // It must be field, then it has annotation, but it not be provider.
                Route paramConfig = element.getAnnotation(Route.class);
                String classFullName = element.toString();
                System.out.println("2====" + paramConfig.path());
                mEntitys.add(new RouteEntity(classFullName, paramConfig.path()));
//                    paramsType.put(StringUtils.isEmpty(paramConfig.path()) ? field.getSimpleName().toString() : paramConfig.name(), typeUtils.typeExchange(field));
            }
        }
    }

}
