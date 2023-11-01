package com.small.o2o.comp.core.utils;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ PathUtils ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/1 22:46
 * @Version ： 1.0
 **/
public class PathUtils {

    public static String geJarPath() {
        String path = null;
        try {
            CodeSource codeSource = PathUtils.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                Path jarPath = Paths.get(codeSource.getLocation().toURI());
                Path jarDirectory = jarPath.getParent();
                System.out.println("项目打包后与 JAR 同级的目录: " + jarDirectory);
                path = jarDirectory.toString();
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;

    }
}
