package com.small.o2o.comp.module.facade;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.excel.CheckCellHandler;
import com.small.o2o.comp.core.excel.MultipleSheelPropety;
import com.small.o2o.comp.core.exception.DataCheckException;
import com.small.o2o.comp.module.facade.base.MetaDataCompare;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.metadata.*;
import com.small.o2o.comp.module.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CompareMetaDataService extends MetaDataCompare {
    @Autowired
    private ObjectInfoService objectInfoService ;
    @Autowired
    private TableListService tableListService ;
    @Autowired
    private TableColumnService tableColumnService ;
    @Autowired
    private TablePrimaryKeyService tablePrimaryKeyService ;
    @Autowired
    private TableIndexService tableIndexService ;
    @Autowired
    private SequencesService sequencesService ;
    @Autowired
    private TypeListService typeListService ;
    @Autowired
    private ProcedureListService procedureListService ;

    @Override
    protected boolean check() {
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();
        if (!ObjectUtil.isNotNull(dscVO)){
            throw new DataCheckException("未发现需要比较的数据源！");
        }
        return true ;
    }

    @Override
    protected List<MultipleSheelPropety> queryData() {
        ArrayList<MultipleSheelPropety> excelList = new ArrayList<>();
        for (O2OConstants.SheetNameEnum sheetEnum : O2OConstants.SheetNameEnum.values()) {

            if (0 == sheetEnum.getIndex()) {
                log.info("开始查0 " + sheetEnum.getDesc());
                //查主要元数据各类信息
                List<OracleObjectInfoVO> tableInfoList = objectInfoService.getObjectInfo();
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(tableInfoList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            } else if (1 == sheetEnum.getIndex()) {
                log.info("开始查1 " + sheetEnum.getDesc());
                //查表信息
                 List<OracleTableInfoVO> tableInfoList = tableListService.getTableInfo();
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(tableInfoList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            } else if (2 == sheetEnum.getIndex()) {
                log.info("开始查表和列");
                long a = System.currentTimeMillis();
                 List<OracleTableColumnFullVO> tableColumnVOList = tableColumnService.getTableColumnFulls() ;
                long b = System.currentTimeMillis();
                log.info("取数据耗时" + (b - a) / 100 + " s");
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(tableColumnVOList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            } else if (3 == sheetEnum.getIndex()) {
                log.info("开始查" + sheetEnum.getDesc());
                //
                List<OracleTableViewVO> tableInfoList = tableListService.getTableViewList();
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(tableInfoList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            }else if (12 == sheetEnum.getIndex()) {
                log.info("开始查 主键" + sheetEnum.getDesc());
                List<OracleTablePrimaryKeyVO> typeList = tablePrimaryKeyService.getTablePrimaryKey(sheetEnum.getCode());
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(typeList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            }
            if (4 == sheetEnum.getIndex()) {
                log.info("开始查索引");
                //第二个sheet数据    此处数据集为手动创建数据  -- 实际开发替换为具体业务逻辑数据
                List<OracleTableIndexVO> tableIndexs = tableIndexService.getTableIndexs("");
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(tableIndexs);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            } else if (5 == sheetEnum.getIndex()) {
                log.info("开始查序列");
                //第二个sheet数据    此处数据集为手动创建数据  -- 实际开发替换为具体业务逻辑数据
                List<OracleSequencesVO> sequencesList = sequencesService.getSequences();
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(sequencesList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);
            } else if (6 == sheetEnum.getIndex()) {
                log.info("开始查 TYPE ");
                List<OracleTypesVO> typeList = typeListService.getTypeList();
                Sheet sheet = new Sheet(sheetEnum.getIndex(), 0);
                sheet.setSheetName(sheetEnum.getDesc());
                MultipleSheelPropety multipleSheelPropety = new MultipleSheelPropety();
                multipleSheelPropety.setData(typeList);
                multipleSheelPropety.setSheet(sheet);
                excelList.add(multipleSheelPropety);

            } else if (9 == sheetEnum.getIndex() || 7 == sheetEnum.getIndex() || 8 == sheetEnum.getIndex()) {
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

    @Override
    protected String generateExcel(String filePath, List<MultipleSheelPropety> excelList) {
        log.info("开始生成Excel ...");
        if (!filePath.endsWith(".xlsx")) {
            String fileName = "元数据收集".concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"))).concat(".xlsx");
            filePath = filePath + fileName;
        }
        ExcelWriter excelWriter = null;
        try {
            List<String> whiteListTable = getWhiteListTable();
            excelWriter = EasyExcel.write(filePath).build();
            for (int i = 0; i < excelList.size(); i++) {
                if (!CollectionUtils.isEmpty(excelList.get(i).getData())) {
                    //这里 需要指定写用哪个class去写
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, excelList.get(i).getSheet().getSheetName())
                            .head(excelList.get(i).getData().get(0).getClass())
                            .registerWriteHandler(new CheckCellHandler(whiteListTable)).build();
                    excelWriter.write(excelList.get(i).getData(), writeSheet);

                }
            }
            //千万别忘记finish 会帮忙关闭流
            excelWriter.finish();
            log.info("文件生成成功！" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return filePath;
    }

    private List<String> getWhiteListTable() {
        return null;
    }


}
