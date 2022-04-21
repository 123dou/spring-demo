package com.dou.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class DynConf {

    private static volatile Config conf;

    private static Set<String> CONFIG_PATH = new HashSet<>();

    private static final Set<URL> URL_SET = new HashSet<>();

    public static Config init() {
        Config load = ConfigFactory.load();
        List<String> configPaths = load.getStringList("config.path");
        String classLoader = DynConf.class.getClassLoader().getResource("").getPath();
        String classpath = new File(classLoader).getAbsolutePath();
        List<PathMatch> pathMatches = configPaths.stream().distinct().map(val -> buildPathMatcher(classpath, val)).collect(Collectors.toList());
        findAllMatchFile(classpath, pathMatches);
        System.out.println("parse path rule: " + String.join(", ", configPaths));
        loadAllConfig(classpath);
        return conf;
    }

    @PostConstruct
    public static void initConf() {
        Config load = ConfigFactory.load();
        List<String> configPaths = load.getStringList("config.path");
        Set<String> confPathSet = new HashSet<>(configPaths);
        findAllMatchURL(confPathSet);
        loadAllConf();
    }

    @Scheduled(fixedDelay = 100000)
    private static void loadAllConf() {
        Config config = null;
        for (URL url : URL_SET) {
            Config temp = ConfigFactory.parseURL(url);
            if (config != null) {
                temp = temp.withFallback(config);
            }
            System.out.println("load config: " + url.getPath());
            config = temp;
        }
        System.out.println("-------------------------------load all config------------------");
        conf = config;
    }

    public static Config conf() {
        return conf;
    }

    private static void findAllMatchURL(Set<String> configPaths) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String configPath : configPaths) {
            Resource[] resources;
            try {
                resources = resolver.getResources(configPath);
                for (Resource resource : resources) {
                    URL_SET.add(resource.getURL());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

    private static void loadAllConfig(String classpath) {
        CONFIG_PATH = CONFIG_PATH.stream().map(val -> val.substring(classpath.length() + 1)).collect(Collectors.toSet());
        Config config = null;
        for (String path : CONFIG_PATH) {
            Config load = ConfigFactory.load(path);
            System.out.println("had load config: " + path);
            if (config != null) {
                load.withFallback(config);
            }
            config = load;
        }
        conf = config;
    }

    private static void findAllMatchFile(String path, List<PathMatch> pathMatches) {
        File file = new File(path);
        if (file.isDirectory()) {
            if (pathMatches.stream().noneMatch(v -> v.isMatch(path, true))) {
                return;
            }
            String[] list = file.list();
            if (list != null) {
                for (String s : list) {
                    findAllMatchFile(path + File.separator + s, pathMatches);
                }
            }
        } else if (file.isFile() && pathMatches.stream().anyMatch(v -> v.isMatch(path, false))) {
            CONFIG_PATH.add(path);
        }
    }

    private static PathMatch buildPathMatcher(String classpath, String path) {
        path = classpath + File.separator + path;
        if (path.contains("\\")) {
            path = replaceFileSeparator(path);
        }
        return new PathMatch(path);
    }

    private static String replaceFileSeparator(String path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '/') {
                sb.append("\\");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
