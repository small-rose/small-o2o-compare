package com.small.o2o.comp.core.excel;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.small.o2o.comp.core.utils.SmallUtils;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
public class CustomRowHandler extends AbstractRowWriteHandler {

    private CellStyle cellStyle ;

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {
        super.beforeRowCreate(writeSheetHolder, writeTableHolder, rowIndex, relativeRowIndex, isHead);
    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        super.afterRowCreate(writeSheetHolder, writeTableHolder, row, relativeRowIndex, isHead);
        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();
        if (isHead){
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()){
                Cell next = cellIterator.next();
                if (next!=null && SmallUtils.hasText(next.getStringCellValue())){
                    next.setCellValue(next.getStringCellValue().replace("OB",dscVO.getDsFirst()));
                    next.setCellValue(next.getStringCellValue().replace("ORA",dscVO.getDsSecond()));
                }
            }
        }

        if (row.getRowNum() == 3 ) {
            Workbook workbook = row.getSheet().getWorkbook();
            cellStyle = workbook.createCellStyle();
            //设置背景颜色
            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            //设置填充模式
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //设置文字颜色
            // 设置字体
            // 设置样式
            cellStyle.setWrapText(true); // 自动换行
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

            // 设置字体
            Font font = workbook.createFont();
            font.setFontName("微软雅黑");
            //font.setFontHeightInPoints((short) 14);
            cellStyle.setFont(font);

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()){
                Cell next = cellIterator.next();
                next.setCellStyle(cellStyle);
            }

         }
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        super.afterRowDispose(writeSheetHolder, writeTableHolder, row, relativeRowIndex, isHead);
    }
}
