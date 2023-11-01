package com.small.o2o.comp.core.utils;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;

import java.io.File;
import java.util.List;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
public class FileRWUtils {

    /**
     * 生成文件
     * @param ddlpath
     * @param ddlList
     */
    public static void fileWriter(String ddlpath, List<String> ddlList){
        File ddl = new File(ddlpath);
        if (ddl.exists()) {
            ddl.delete();
        }
        FileWriter fileWriter = new FileWriter(ddl);
        fileWriter.appendLines(ddlList);
    }


    /**
     * 读取文件
     * @param ddlpath
     * @param
     */
    public static List<String> fileReader(String ddlpath){
        File ddl = new File(ddlpath);
        FileReader fileReader = new FileReader(ddl);
        return fileReader.readLines();
    }

}
