package com.dou.dynconfhocon;

import com.google.common.eventbus.EventBus;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class DynConf {

    private static volatile Config conf;

    private static final Set<String> CONFIG_PATH = new HashSet<>();

    private static final Set<Path> PATH_SET = new HashSet<>();

    private static final EventBus EVENT_BUS = new EventBus();

    private static final ConcurrentHashMap<String, Object> SUBSCRIBE_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public static void initConf() throws IOException {
        Config load = ConfigFactory.load();
        List<String> configPaths = load.getStringList("config.path");
        Set<String> confPathSet = new HashSet<>(configPaths);
        findAllMatchURL(confPathSet);
        loadAllConf();
    }

    @Scheduled(fixedDelay = 100000)
    private static void loadAllConf() {
        Config config = null;
        for (Path path : PATH_SET) {
            Config temp = ConfigFactory.parseFile(path.toFile());
            if (config != null) {
                temp = temp.withFallback(config);
            }
            System.out.println("load config: " + path);
            config = temp;
        }
        System.out.println("-------------------------------load all config------------------");
        conf = config;
        updateEvent();
    }

    public static void registerDynConf(String path, Object obj) {
        SUBSCRIBE_MAP.put(path, obj);
        EVENT_BUS.register(obj);
    }

    private static void updateEvent() {
        SUBSCRIBE_MAP.forEach((key, val) -> EVENT_BUS.post(val));
    }

    public static Config conf() {
        return conf;
    }

    private static void findAllMatchURL(Set<String> configPaths) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String configPath : configPaths) {
            Resource[] resources;
            resources = resolver.getResources(configPath);
            for (Resource resource : resources) {
                PATH_SET.add(Paths.get(resource.getURI()));
            }
        }
    }

}
