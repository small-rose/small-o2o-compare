package com.small.o2o.comp.module.param;

import com.small.o2o.comp.core.utils.PathUtils;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 19:16
 * @version: v1.0
 */

@Builder
@Data
public class DsCompareParam {

    private static final String DEFAULT_PATH =  "";

    /**
     *  must
     */
    private String dsFirst;
    /**
     *  must
     */
    private String dsSecond ;

     private String table ;

    /**
     * 比较时可以以mainDs的数据为标准
     * 可在表 和 表的列比较的时生效
     */
    private String mainDs ;

    private boolean tableColumnDDL ;

    private String generateExcelPath ;
    private String ext1 ;


    public String getGenerateExcelPath() {
        if (!StringUtils.hasText(generateExcelPath)){
            generateExcelPath = PathUtils.geJarPath();
        }
        return generateExcelPath;
    }
}
