package com.bxtdata.interview.interview.controller;

import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;
import com.bxtdata.interview.interview.service.CommodityService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@Validated
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    @GetMapping("/breakPriceUrls")
    public HashMap<String,Object> getBreakPriceUrls(@RequestParam @NotEmpty String batchNo, @RequestParam @NotEmpty String platform, @RequestParam(defaultValue = "10")Integer count){
        List<BreakRecordPO> breakRecords = commodityService.getBreakRecords(batchNo, platform, count);
        HashMap<String, Object> res = new HashMap<>();
        res.put("data",breakRecords);
        res.put("status",true);
        return res;
    }

}
