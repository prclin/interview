package com.bxtdata.interview.interview.mapper;

import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

@Mapper
public interface BreakRecordMapper {

    @SelectProvider(type = BreakRecordProvider.class, method = "selectBy")
    List<BreakRecordPO> selectBy(@Param("batchNo") String batchNo, @Param("platformName") String platformName, @Param("pulledIds") long[] pulledIds, @Param("count") int count);

    @Update("update break_record set state =#{state} where id = #{id}")
    void updateStateById(@Param("id") long id, @Param("state") int state);

    class BreakRecordProvider {
        public String selectBy(@Param("batchNo") String batchNo, @Param("platformName") String platformName, @Param("pulledIds") long[] pulledIds, @Param("count") int count) {
            return new SQL() {
                {
                    SELECT("id,batch_no,platform_name,page_url,sku_id,state");
                    FROM("break_record");
                    WHERE("batch_no=#{batchNo}");
                    WHERE("platform_name=#{platformName}");
                    WHERE("state=0");
                    if (pulledIds.length != 0) {
                        StringBuilder sb = new StringBuilder("(");
                        for (int i = 0; i < pulledIds.length; i++) {
                            sb.append(pulledIds[i]);
                            if (i < pulledIds.length - 1) sb.append(",");
                        }
                        sb.append(")");
                        WHERE("id not in " + sb);
                    }
                    LIMIT("#{count}");
                }
            }.toString();
        }
    }
}
