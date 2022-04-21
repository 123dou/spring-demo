package com.dou.controller;

import com.dou.config.DynConf;
import com.github.racc.tscg.TypesafeConfigModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.dou.config.bean.Person;
import com.netflix.config.DynamicProperty;
import com.typesafe.config.ConfigBeanFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
public class HelloWorldController {

    @ApiOperation(value = "hello world!", httpMethod = "GET")
    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }

    /**
     * 三个系统配置
     * <p>
     * archaius.configurationSource.additionalUrls: 可以设置加载的配置路径
     * archaius.configurationSource.defaultFileName: 表示加载的配置文件的默认值
     * archaius.fixedDelayPollingScheduler.initialDelayMills: 从配置源读取的初始延迟
     * archaius.fixedDelayPollingScheduler.delayMills: 固定读取配置URL之间的延迟（以毫秒为单位）
     *
     * @return
     */
    @ApiOperation(value = "测试动态配置文件 archaius", httpMethod = "GET")
    @RequestMapping("/test/archaius")
    public String test_archaius() {
        System.setProperty("archaius.configurationSource.additionalUrls", "classpath:config.properties,classpath:demo.properties");

        String demo = DynamicProperty.getInstance("demo").getString();
        System.out.println(demo);


        String test = DynamicProperty.getInstance("test").getString();
        System.out.println(test);
        return demo + test;
    }


    @ApiOperation(value = "测试hocon", httpMethod = "GET")
    @RequestMapping("/test/hocon")
    public String test_hocon() {
        Injector injector = Guice.createInjector(TypesafeConfigModule.fromConfigWithPackage(DynConf.conf(), "com.dou.config.bean"));
        Person person = injector.getInstance(Person.class);
        Person person1 = ConfigBeanFactory.create(DynConf.conf().getConfig("person"), Person.class);
        return person1.toString();
    }


}