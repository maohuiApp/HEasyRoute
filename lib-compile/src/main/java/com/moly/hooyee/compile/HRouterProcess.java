package com.moly.hooyee.compile;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.xml.transform.Transformer;

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
        for (RouteEntity entity : mEntities) {
            if (entity.getInterceptName() == null || "".equals(entity.getInterceptName())) {
                registerBuild.addStatement("map.put($S, $T.build($S, $S, $S))",
                        entity.getPath(),
                        activity,
                        entity.getClassName(),
                        entity.getPath(),
                        "module"
                );
            } else {
                String fullName = entity.getInterceptName();
                int index = fullName.lastIndexOf(".");
                String packageName = fullName.substring(0, index);
                String className = fullName.substring(index + 1, fullName.length());
                System.out.println("2====" + packageName + ";" + className);

                ClassName clazz = ClassName.get(packageName, className);
                registerBuild.addStatement("map.put($S, $T.build($S, $S, $S, $S, new $T()))",
                        entity.getPath(),
                        activity,
                        entity.getClassName(),
                        entity.getPath(),
                        "module",
                        entity.getInterceptName(),
                        clazz
                );
            }
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
            String routeIntercept = "";
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
                            TypeMirror typeMirror = (TypeMirror) value;
                            System.out.printf(">> classValue: %s\n", typeMirror.toString());
                            routeIntercept = value.toString();
                            break;
                        case "intercepts":
                            List<? extends AnnotationValue> typeMirrors = (List<? extends AnnotationValue>) value;
                            for (AnnotationValue v : typeMirrors) {
                                v.getValue().toString();
                            }
                            System.out.printf(">> classesValue: %s\n",
                                    ((TypeMirror) typeMirrors.get(0).getValue()).toString());
                            break;
                    }
                }
            }
            mEntities.add(new RouteEntity(classFullName, path, routeIntercept));

        }
    }

}
