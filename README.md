# HEasyRoute
一个用于帮助组件化解耦的轻量框架

## 导入
如下添加依赖到各模块的build.gradle:

```gradle
1、添加依赖
dependencies {
  annotationProcessor 'com.hooyee:easyroute-compile:1.0.1'
  compile 'com.hooyee:easyroute-lib:1.0.1'
}

2、添加区分各模块的参数
android {
...
       javaCompileOptions {
          annotationProcessorOptions {
              arguments = [ moduleName : project.getName() ]
          }
       }
...
}
```

## 简单使用
1. 在Application#onCreate()中调用EasyRoute.init(this);

2. 在需要路由配置的Activity的类定义上加上注解：@Route(path = "/test")

3. 在需要跳转到路由所标注的Activity的地方调用：EasyRoute.navigation(context, "/test");

完成以上三步即可跳转到指定的Activity,跨模块调用同样可行

需要注意的是，每个moudle都需要配置 javaCompileOptions参数
## License

   ```license
   Copyright [2016] [MaoHui of copyright owner]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   ```

