package com.xaeport.crossborder.data.mapper;

import com.xaeport.crossborder.data.entity.ImpInventoryBody;
import com.xaeport.crossborder.data.entity.ImpInventoryHead;
import com.xaeport.crossborder.data.provider.DetailImportSQLProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface DetailImportMapper {


    /*
     * 导入插入ImpInventoryHead数据
     */
    @InsertProvider(type = DetailImportSQLProvider.class, method = "insertImpInventoryHead")
    boolean insertImpInventoryHead(@Param("impInventoryHead") ImpInventoryHead impInventoryHead) throws Exception;

    /*
     * 导入插入impInventoryBody数据
     */
    @InsertProvider(type = DetailImportSQLProvider.class, method = "insertImpInventoryBody")
    boolean insertImpInventoryBody(@Param("impInventoryBody") ImpInventoryBody impInventoryBody) throws Exception;

    /*
     * 查询有无重复订单号表头信息
     */
    @SelectProvider(type = DetailImportSQLProvider.class, method = "isRepeatOrderNo")
    int isRepeatOrderNo(@Param("impInventoryHead") ImpInventoryHead impInventoryHead) throws Exception;


}
