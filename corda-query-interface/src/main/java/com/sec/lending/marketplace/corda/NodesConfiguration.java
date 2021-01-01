package com.sec.lending.marketplace.corda;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class NodesConfiguration {

    @Autowired
    private CordaNodesRepository cordaNodesRepository;
    private static String fileName = "cordaNodes.csv";

    @Bean
    public List<CordaNodes> initializeNodes() {

        List<CordaNodes> list = new ArrayList();
        System.out.println("Current working directory ------ " + System.getProperty("user.dir"));
        if (!new File(fileName).exists()) {
            fileName = "../" + fileName;
        }
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> {
                if (!StringUtils.isEmpty(line.trim())) {
                    String[] split = line.split("\\|");
                    List<Roles> roles = new ArrayList<>();
                    for (String role : split[3].split(",")) {
                        if (!StringUtils.isEmpty(role.trim()))
                            roles.add(Roles.valueOf(role.trim()));
                    }
                    String restEndPoint = "";
                    if (!StringUtils.isEmpty(split[2])) {
                        restEndPoint = split[2].trim();
                        restEndPoint = restEndPoint.startsWith("http") ? restEndPoint : "http://" + restEndPoint;
                    }
                    CordaNodes node = CordaNodes.builder()
                            .cordaName(split[0])
                            .shortName(split[1])
                            .restEndPoint(restEndPoint)
                            .roles(roles).build();
                    list.add(node);
                    if(cordaNodesRepository.findFirstByShortName(node.getShortName()) == null) {
                        cordaNodesRepository.save(node);
                    }
                }
            });
        } catch (Exception e) {
            throw new BeanInitializationException("Initialization exception ", e);
        }
        return list;
    }
}
