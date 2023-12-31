package com.small.o2o.comp.module.compare;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.small.o2o.comp.core.excel.MultipleSheelPropety;
import com.small.o2o.comp.module.param.DsCompareParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
@Slf4j
public class ExcelGenaratorService {

    @Autowired
    private CompareMetaDataService compareMetaDataService ;


    public void doHandle(DsCompareParam dsCompare) {
        compareMetaDataService.doCompareHandler(dsCompare);
    }

    public void doHandle(HttpServletResponse response) {

        compareMetaDataService.check();
        List<MultipleSheelPropety> excelList = compareMetaDataService.queryData();

        log.info("开始生成Excel ...");
        String fileName = new String("元数据收集.xlsx".getBytes(), StandardCharsets.ISO_8859_1);
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            for (int i = 0; i < excelList.size(); i++) {
                if (!CollectionUtils.isEmpty(excelList.get(i).getData())) {

                    //这里 需要指定写用哪个class去写
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, excelList.get(i).getSheet().getSheetName()).head(excelList.get(i).getData().get(0).getClass()).build();
                    excelWriter.write(excelList.get(i).getData(), writeSheet);
                }
            }
            //千万别忘记finish 会帮忙关闭流
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }


}
