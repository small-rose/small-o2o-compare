package com.small.o2o.comp.module.service.metadata;


import cn.hutool.core.util.StrUtil;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaocai
 */
@Slf4j
@Service
public class TableColumnService {


    @Autowired
    private QueryMetaService queryMetaService;

    /**
     * 表的列对比
     *
     * @return
     */
    public List<OracleTableColumnFullVO> getTableColumnFulls() {
        List<String> tableList = new ArrayList<>();
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();
        List<ObTableInfoVO> allTableList = MetaDataContextHolder.getAllTableList();
        if (CollectionUtils.isEmpty(allTableList)){
            List<String> tempList = new ArrayList<>();
            //重新查询
            DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).tableName(dscVO.getTable()).build();
            List<ObTableInfoVO> obObjList = queryMetaService.queryTableInfo(queryPramsVO);
            obObjList.forEach(t-> tempList.add(t.getTableName()));

            DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).tableName(dscVO.getTable()).build();
            List<ObTableInfoVO> obObjList2 = queryMetaService.queryTableInfo(queryPramsVO2);;
            obObjList2.forEach(t-> tempList.add(t.getTableName()));

            tableList = tempList.stream().distinct().collect(Collectors.toList());

        }else{

            DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).tableName(dscVO.getTable()).build();
            List<ObTableInfoVO> obObjList2 = queryMetaService.queryTableInfo(queryPramsVO);;
            for (ObTableInfoVO tableInfoVO : obObjList2) {
                tableList.add(tableInfoVO.getTableName());
            }
        }

        List<OracleTableColumnFullVO> resultList = new ArrayList<>();

        List<String> allNames = new ArrayList<>();
        int tableNo = 1;
        OracleTableColumnFullVO object = null;

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).build();

        for (String tableName : tableList) {
            queryPramsVO.setTableName(tableName);
            List<ObTableColumnFullVO> obObjList = queryMetaService.queryTableColumnFullVO(queryPramsVO);
            queryPramsVO.setTableName(tableName);
            List<ObTableColumnFullVO> oraObjList = queryMetaService.queryTableColumnFullVO(queryPramsVO2);

            Map<String, ObTableColumnFullVO> obObjMap = obObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName().concat(o.getColumnName()), (p) -> p));

            Map<String, ObTableColumnFullVO> oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName().concat(o.getColumnName()), Function.identity()));

            for (ObTableColumnFullVO p : obObjList) {
                allNames.add(p.getTableName().concat(p.getColumnName()));
            }
            for (ObTableColumnFullVO p : oraObjList) {
                if (!allNames.contains(p.getTableName().concat(p.getColumnName()))) {
                    allNames.add(p.getTableName().concat(p.getColumnName()));
                }
            }

            int indexNo = 1;
            log.info("{} Table - {} 取并集Column 大小为 {}", tableNo, tableName, allNames.size());
            tableNo++;
            for (String n : allNames) {
                object = new OracleTableColumnFullVO();

                ObTableColumnFullVO tmpob = obObjMap.get(n);
                ObTableColumnFullVO tmpora = oracleObjMap.get(n);
                //if (obList.size() > 0){
                if (tmpob != null) {
                    ObTableColumnFullVO ob = tmpob;
                    object.setTableName(ob.getTableName());
                    object.setColumnName(ob.getColumnName());
                    object.setDataType(ob.getDataType());
                    object.setExtend(ob.getExtend());
                    object.setNullable(ob.getNullable());
                    object.setDataDefault(ob.getDataDefault());
                    tableName = ob.getTableName();
                }
                //if (oraList.size() > 0 ){
                if (tmpora != null) {
                    //ObTableColumnVO oracle = oraList.get(0);
                    ObTableColumnFullVO oracle = tmpora;
                    object.setTableName2(oracle.getTableName());
                    object.setColumnName2(oracle.getColumnName());
                    object.setDataType2(oracle.getDataType());
                    String ext2 = oracle.getExtend();
                    //System.out.println("db :" + ext2);
                    if (StringUtils.hasText(oracle.getExtend()) && oracle.getExtend().contains("%")) {
                        if (oracle.getDataDefault()!=null && oracle.getDataDefault().startsWith("0.00")){
                            oracle.setDataDefault("0");
                        }
                        if (oracle.getDataDefault()!=null && oracle.getDataDefault().equals("000000000000")){
                            oracle.setDataDefault("0");
                        }
                        ext2 = String.format(oracle.getExtend(), oracle.getDataDefault().trim());
                    }
                    //System.out.println(ext2);
                    object.setExtend2(ext2);
                    object.setNullable2(oracle.getNullable());
                    object.setDataDefault2(oracle.getDataDefault());
                    tableName = oracle.getTableName();
                }

                object.setNo(String.valueOf(indexNo));
                object.setNo2(String.valueOf(indexNo));
                //System.out.println(object);
                resultList.add(object);
                indexNo++;
            }
            allNames.clear();
            //ddlList.add("-- " + tableName);
            //ddlList.add("-- ");
        }

        if (dscVO.isTableColumnDDL()){
            genaratorDDLs(resultList);
        }
        return resultList ;
    }


    String DDL_ADD = "ALTER TABLE %s ADD %s %s %s;";
    String DDL_MODIFY = "ALTER TABLE %s MODIFY %s %s ";
    /**
     * 表的列对比
     *
     * @return
     */
    public List<String> genaratorDDLs(List<OracleTableColumnFullVO> resultList) {
        List<String> ddlList = new ArrayList<>();
        String ddlpath = "E:\\obgenerator\\ora_ddl.sql";
        List<String> allNames = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        String tableName = "";
        for (OracleTableColumnFullVO object : resultList) {
            tableName = object.getTableName();

            if (StringUtils.hasText(object.getTableName()) && StringUtils.hasText(object.getTableName2()) && StringUtils.hasText(object.getColumnName()) && !StringUtils.hasText(object.getColumnName2())) {
                // ob 有 oracle 没有,需要补全
                String format = StrUtil.format("表 {} 的列 {} OB有 ORACLE 没有 ！！！ 缺列！！！", object.getTableName(), object.getColumnName());
                System.out.println(format);
                String ddl = String.format(DDL_ADD, tableName, object.getColumnName(), object.getDataType(), object.getExtend());
                ddlList.add(ddl);
            }
            if (!StringUtils.hasText(object.getTableName()) && !StringUtils.hasText(object.getColumnName()) && StringUtils.hasText(object.getColumnName2())) {
                // ob 没有 oracle 有,需要确认，然后删除或同步
                String format = StrUtil.format("-- 表 {} 的列 {} OB沒有 ORACLE 有 ！！！ 多列！！！", object.getTableName2(), object.getColumnName2());
                System.out.println(format);
                ddlList.add(format);
            }
            sb.setLength(0);
            if (object.getTableName() != null && object.getTableName().equals(object.getTableName2()) && object.getColumnName().equals(object.getColumnName2())) {
                if (object.getColumnName().equals(object.getColumnName2()) && !object.getDataType().equals(object.getDataType2())) {
                    String format = StrUtil.format("表 {} 的列 {} 类型不一致 OB : {}  ORACLE : {} 类型長度差异", object.getTableName(), object.getColumnName(), object.getDataType(), object.getDataType2());
                    System.out.println(format);
                    String ddl = String.format(DDL_MODIFY, tableName, object.getColumnName(), object.getDataType());
                    sb.append(ddl);
                    if (StringUtils.hasText(object.getDataDefault()) && !object.getDataDefault().equals(object.getDataDefault2())) {
                        sb.append(" DEFAULT ").append(object.getDataDefault());
                    }
                    if (!object.getNullable().equals(object.getNullable2()) && "N".equals(object.getNullable())) {
                        sb.append(" NOT NULL ");
                    }
                    sb.append(";");
                    ddlList.add(sb.toString());
                }
            }
        }
        allNames.clear();

        /*File ddl = new File(ddlpath);
        if (ddl.exists()) {
            ddl.delete();
        }
        FileWriter fileWriter = new FileWriter(ddl);
        fileWriter.appendLines(ddlList);*/
        return ddlList;
    }



}
