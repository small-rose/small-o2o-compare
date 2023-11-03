package com.small.o2o.comp.module.compare;


import com.alibaba.excel.metadata.Sheet;
import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.core.excel.MultipleSheelPropety;
import com.small.o2o.comp.module.compare.base.CommonGenerater;
import com.small.o2o.comp.module.service.oracle.MetaProcedureListService;
import com.small.o2o.comp.module.vo.OracleProcedureVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  xiaocai
 */
@Slf4j
 public class ProcedureFacadeService  extends CommonGenerater {

    @Autowired
    private FilePickService filePickService;
    @Autowired
    private MetaProcedureListService procedureListService ;


    public void doHandle(String filePath) {
        List<MultipleSheelPropety> excelList = getDatas();
        generaterExcel(filePath, excelList);
    }

    public void doHandle2(String filePath) {
        List<MultipleSheelPropety> excelList  = getDatas2();
        generaterExcel(filePath, excelList);
    }

    private List<MultipleSheelPropety>  getDatas() {
        ArrayList<MultipleSheelPropety> excelList = new ArrayList<>();
        for (MetaBuzTypeEnum sheetEnum : MetaBuzTypeEnum.values()) {

             if (9 == sheetEnum.getIndex() || 7 == sheetEnum.getIndex() || 8 == sheetEnum.getIndex()) {
                log.info("开始查 " + sheetEnum.getCode());
                List<OracleProcedureVO> typeList = procedureListService.getProcedureList(sheetEnum.getCode());
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(typeList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            }
        }
        return excelList;
    }

    private List<MultipleSheelPropety> getDatas2() {
        ArrayList<MultipleSheelPropety> excelList = new ArrayList<>();

        for (MetaBuzTypeEnum sheetEnum : MetaBuzTypeEnum.values()) {

            if (13 == sheetEnum.getIndex() ) {
                log.info("开始查 " + sheetEnum.getCode());
                List<OracleProcedureVO> typeList = procedureListService.getProcedureList(sheetEnum.getCode());
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(typeList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            }

        }

        return excelList;
    }
}
