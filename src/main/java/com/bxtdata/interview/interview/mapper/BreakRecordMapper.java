package com.bxtdata.interview.interview.mapper;

import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

@Mapper
public interface BreakRecordMapper {

    @Select("select id,batch_no,platform_name,page_url,sku_id,state from break_record where batch_no=#{batchNo} and platform_name=#{platformName} and state=0 limit #{count} for update")
    List<BreakRecordPO> selectForUpdateBy(@Param("batchNo") String batchNo, @Param("platformName") String platformName, @Param("count") int count);

    @Update("update break_record set state =#{state} where id = #{id}")
    void updateStateById(@Param("id") long id, @Param("state") int state);

    @UpdateProvider(type = BreakRecordProvider.class, method = "BatchUpdateStateById")
    void BatchUpdateStateById(long[] ids, @Param("state") int state, @Param("exceptedState") int exceptedState);

    class BreakRecordProvider {

        public String BatchUpdateStateById(long[] ids, @Param("state") int state, @Param("exceptedState") int exceptedState) {
            return new SQL() {
                {
                    UPDATE("break_record");
                    SET("state=#{state}");
                    StringBuilder sb = new StringBuilder();
                    sb.append("(");
                    for (long id : ids) {
                        sb.append(id);
                        sb.append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(")");
                    WHERE("id in " + sb);
                    WHERE("state=#{exceptedState}");
                }
            }.toString();
        }
    }
}
