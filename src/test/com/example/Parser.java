package com.example;

import net.openhft.compiler.CompilerUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Parser {

    private String className = "com.example.AbcApp";
    private String javaCode = "package com.example;\n" +
            "\n" +
            "import java.util.UUID;\n" +
            "import com.example.entity.BaseEntity;\n" +
            "public class AbcApp extends BaseEntity{\n" +
            "        private UUID uuid;\n" +
            "}\n";
    @Test
    public void generate() throws IllegalAccessException, ClassNotFoundException {
        CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
    }

}
