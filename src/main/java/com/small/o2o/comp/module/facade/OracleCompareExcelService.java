package com.small.o2o.comp.module.facade;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j

@Service
public class OracleCompareExcelService {

    @Autowired
    private CompareMetaDataService compareMetaDataService ;


   public void compareGenerateExcel(String path) {
      compareMetaDataService.doCompareHandler(null);
   }
}
