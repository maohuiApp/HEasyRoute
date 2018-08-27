package com.moly.hooyee.compile;

import com.google.auto.service.AutoService;
import com.moly.hooyee.annocation.Route;
import com.moly.hooyee.model.RouteEntity;
import com.squareup.javapoet.ClassName;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
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
    public List<RouteEntity> mEntities;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mEntities = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        parseRoutes(roundEnv.getElementsAnnotatedWith(Route.class));
        ClassName activity = ClassName.get("com.moly.hooyee.model", "RouteEntity");
        ClassName intercept = ClassName.get("com.moly.hooyee.model", "RouteIntercept");
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteEntity.class)
        );

        MethodSpec.Builder registerBuild = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(inputMapTypeOfGroup, "map")
                .addStatement("$T<$T> intercepts = new $T()", List.class, intercept, ArrayList.class);
        for (RouteEntity entity : mEntities) {
            List<String> fullName = entity.getInterceptName();
            for (String name : fullName) {
                System.out.println("name = " + name);
                int index = name.lastIndexOf(".");
                String packageName = name.substring(0, index);
                String className = name.substring(index + 1, name.length());
                ClassName clazz = ClassName.get(packageName, className);
                registerBuild.addStatement("intercepts.add(new $T());", clazz);
            }

            registerBuild.addStatement("map.put($S, $T.build($S, $S, intercepts))",
                    entity.getPath(),
                    activity,
                    entity.getClassName(),
                    entity.getPath()
            )
            .addStatement("intercepts = new ArrayList()");
        }


        MethodSpec register = registerBuild.build();

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

    private void parseRoutes(Set<? extends Element> annotations) {
        for (Element element : annotations) {

            System.out.println("From the Annotation element:");
            // Get the annotation element from the type element
            List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
            String classFullName = "";
            String path = "";
            List<String> intercepts = new ArrayList<>();
            if (element.getKind().isClass()) {
                // It must be field, then it has annotation, but it not be provider.
                Route paramConfig = element.getAnnotation(Route.class);
                classFullName = element.toString();
                path = paramConfig.path();
            }
            for (AnnotationMirror annotationMirror : annotationMirrors) {

                // Get the ExecutableElement:AnnotationValue pairs from the annotation element
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                        = annotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                        : elementValues.entrySet()) {
                    String key = entry.getKey().getSimpleName().toString();
                    Object value = entry.getValue().getValue();
                    switch (key) {
                        case "intercept":
                            List<? extends AnnotationValue> typeMirrors = (List<? extends AnnotationValue>) value;
                            for (AnnotationValue v : typeMirrors) {
                                intercepts.add(v.getValue().toString());
                            }
                            break;
                    }
                }
            }
            System.out.println(intercepts);
            RouteEntity entity = new RouteEntity(classFullName, path);
            entity.setInterceptName(intercepts);
            mEntities.add(entity);
        }
    }

}
