package com.dou.dynconfhocon;

import com.github.racc.tscg.TypesafeConfigModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.ConfigBeanFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping(path = "/config")
public class HoconConfController {

    @ApiOperation(value = "测试hocon", httpMethod = "GET", notes = "默认会加载classpath*:application.conf")
    @RequestMapping("/hocon")
    public String test_hocon() {
        Person person = ConfigBeanFactory.create(DynConf.conf().getConfig("person"), Person.class);
        return person.toString();
    }

    @ApiOperation(value = "测试hocon配置通过注解自动注入到bean", httpMethod = "GET")
    @RequestMapping("/hocon/annotaion")
    public String test_hocon_annotation() {
        Injector injector = Guice.createInjector(TypesafeConfigModule.fromConfigWithPackage(DynConf.conf(), "com.dou.dynconfhocon"));
        Person person = injector.getInstance(Person.class);
        return person.toString();
    }





}