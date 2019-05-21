package com.example.support;

import com.example.entity.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RepositoryGeneratorListener {

    private static final transient Logger logger = LoggerFactory.getLogger(RepositoryGeneratorListener.class);

    private Set<Class<?>> unMappedWithRepoEntity = new HashSet<>();

    @EventListener
    private void initRepositoryMapping(ApplicationReadyEvent event) {
        unMappedWithRepoEntity.add(Department.class);
        unMappedWithRepoEntity.forEach((clazz) -> {
            String repoPackageString = getRepositoryPackageName(clazz);
            String className = repoPackageString + "." + clazz.getSimpleName() + "Repository";
            String javaCode = generateRepositoryString(clazz, repoPackageString);
            logger.info("generating java class : {} code:{}", className, javaCode);
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                logger.info("using classloader: {}", classLoader);
                CompilerUtils.loadFromJava(classLoader, className, javaCode);
                System.out.println(Class.forName("com.example.repository.DepartmentRepository"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private String generateRepositoryString(Class<?> clazz, String repoPackageString) {
        return "package " + repoPackageString + ";" +
                "\nimport " + clazz.getName() + ";" +
                "\nimport org.springframework.stereotype.Repository;" +
                "\nimport java.util.UUID;" +
                "\nimport com.example.repository.BaseRepository;" +
                "\n@Repository" +
                "\npublic interface " +
                clazz.getSimpleName() +
                "Repository extends BaseRepository<" +
                clazz.getSimpleName() +
                ", UUID> {}";
    }

    private static String getRepositoryPackageName(Class clazz) {
        String[] arr = clazz.getPackage().getName().split("\\.");
        arr[arr.length - 1] = "repository";
        StringBuilder repositoryPackage = new StringBuilder();
        for(String s : arr) {
            repositoryPackage.append(s);
            repositoryPackage.append(".");
        }
        return repositoryPackage.deleteCharAt(repositoryPackage.length() - 1).toString();
    }

}
