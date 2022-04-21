package com.dou.config.bean;

import com.github.racc.tscg.TypesafeConfig;
import com.google.inject.Inject;
import com.typesafe.config.Optional;
import lombok.Data;

import java.util.List;

@Data
public class Person {
    @Inject
    @TypesafeConfig("person.name")
    private String name;
    @Inject
    @TypesafeConfig("person.age")
    private int age;
    @Optional
    private List<String> houseList;
    @Optional
    private int b;
}
