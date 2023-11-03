package com.bxtdata.interview.interview.service;

import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;

import java.util.List;

public interface CommodityService {
    List<BreakRecordPO> getBreakRecords(String batchNo, String platform, Integer count);
}
