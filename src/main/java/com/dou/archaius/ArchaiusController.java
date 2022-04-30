package com.dou.archaius;

import cn.hutool.core.util.StrUtil;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicProperty;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.sources.URLConfigurationSource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Source;
import java.net.URL;

@Api
@RestController
@RequestMapping(path = "archaius")
public class ArchaiusController {

    /**
     * 三个系统配置
     * <p>
     * archaius.configurationSource.additionalUrls: 可以设置加载的配置路径
     * archaius.configurationSource.defaultFileName: 表示加载的配置文件的默认值
     * archaius.fixedDelayPollingScheduler.initialDelayMills: 从配置源读取的初始延迟
     * archaius.fixedDelayPollingScheduler.delayMills: 固定读取配置URL之间的延迟（以毫秒为单位）
     *
     */
    @ApiOperation(value = "测试动态配置文件 archaius", httpMethod = "GET")
    @RequestMapping("/archaius")
    public String test_archaius() {
        System.setProperty("archaius.configurationSource.additionalUrls", "classpath:archaius/config.properties");
        // 设置系统变量必须要在DynamicProperty初始化前,路径不支持模糊匹配
        System.setProperty("archaius.configurationSource.additionalUrls", "classpath:archaius/config.properties,classpath:archaius/other/demo.properties");
        String demo = DynamicProperty.getInstance("demo").getString();
        String getDemo = StrUtil.format("get key: demo={} from default config: config.properties", demo);
        System.out.println(getDemo);
        String getTest = DynamicProperty.getInstance("test").getString();
        System.out.println(getTest);
        return String.join(System.lineSeparator(), getDemo,  getTest);
    }


    @ApiOperation(value = "测试动态配置文件 archaius", httpMethod = "GET", notes = "archaius 初始化后再动态加载配置文件")
    @RequestMapping("/archaius/dyn_add_config")
    public String archaius_dyn_add_config() {
        System.setProperty("archaius.configurationSource.additionalUrls", "classpath:archaius/config.properties");
        String demo = DynamicProperty.getInstance("demo").getString();
        String getDemo = StrUtil.format("get key: demo={} from default config: config.properties", demo);
        System.out.println(getDemo);
        URL url = ArchaiusController.class.getResource("/archaius/other/demo.properties");
        URLConfigurationSource source = new URLConfigurationSource(url);
        DynamicConfiguration dynamicConfiguration = new DynamicConfiguration(source, new FixedDelayPollingScheduler());
        ConfigurationManager.loadPropertiesFromConfiguration(dynamicConfiguration);
        String getTest = DynamicProperty.getInstance("test").getString();
        System.out.println(getTest);
        return String.join(System.lineSeparator(), getDemo,  getTest);
    }






}