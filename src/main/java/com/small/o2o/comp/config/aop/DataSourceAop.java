package com.small.o2o.comp.config.aop;

import com.small.o2o.comp.config.datasource.DynamicDSContextHolder;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 19:37
 * @version: v1.0
 */

@Slf4j
@Component
@Aspect
public class DataSourceAop {


    @Pointcut("@annotation(com.small.o2o.comp.config.annotation.DynamicDataSource)")
    public void servicePoint() {

    }

    @Around(value = "servicePoint() ")
    public void arroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();
            DSQueryPramsVO queryPramsVO = (DSQueryPramsVO) args[0];
            DynamicDSContextHolder.setDataSourceType(queryPramsVO.getDataSourceName());
            log.info("执行到了切面了......");
            joinPoint.proceed();
        }catch (Exception e){

        }finally {
            DynamicDSContextHolder.removeDataSourceType();
        }


    }

}
