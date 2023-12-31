package com.small.o2o.comp.module.service.meta;

import com.small.o2o.comp.core.exception.QueryException;
import com.small.o2o.comp.core.utils.CamelCaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 16:48
 * @version: v1.0
 */

@Slf4j
@Service
public class JdbcTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate ;


    public void execute(String sql){
        if (log.isDebugEnabled()){
            log.debug("EXECUTE OceanBase SQL : \n{}\n------------------------",sql);

        }
        jdbcTemplate.execute(sql);
    }

    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) {
        final List<T> result = new ArrayList<>();
        //AtomicInteger no = new AtomicInteger();
        jdbcTemplate.query(sql,  rs -> {
            try {
                // 字段名称
                List<String> columnNames = new ArrayList<>();
                ResultSetMetaData meta = rs.getMetaData();
                int num = meta.getColumnCount();
                for (int i = 0; i < num; i++) {
                    columnNames.add(meta.getColumnLabel(i + 1));
                }
                // 设置值
                do {
                    T obj = clazz.getConstructor().newInstance();
                    for (int i = 0; i < num; i++) {
                        // 获取值
                        Object value = rs.getObject(i + 1);
                        // table.column形式的字段去掉前缀table.
                        String columnName = resolveColumn(columnNames.get(i));
                        // 下划线转驼峰
                        String property = CamelCaseUtils.toCamelCase(columnName);
                        // 复制值到属性，这是spring的工具类
                        BeanUtils.copyProperty(obj, property, value);
                    }
                    //no.set(no.get() + 1);
                    //BeanUtils.copyProperty(obj, "no", no.get());

                    result.add(obj);
                } while (rs.next());
            } catch (Exception e) {
                e.printStackTrace();
                throw new QueryException("查询报错了");
            }
        }, params);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;
    }


    private String resolveColumn(String column) {
        final int notExistIndex = -1;
        int index = column.indexOf(".");
        if (index == notExistIndex) {
            return column;
        }
        return column.substring(index + 1);
    }



    public <T> T queryOneColumn(String sql, Class<T> clazz, Object... params) {
        T result;
        if (ArrayUtils.isEmpty(params)) {
            result = jdbcTemplate.queryForObject(sql, clazz);
        } else {
            result = jdbcTemplate.queryForObject(sql, clazz, params);
        }
        return result;
    }

}
