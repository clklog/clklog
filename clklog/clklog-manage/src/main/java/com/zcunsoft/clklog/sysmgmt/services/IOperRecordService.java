package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.dto.ListWithTotalCount;
import com.zcunsoft.clklog.sysmgmt.dto.OperRecordDTO;
import com.zcunsoft.clklog.sysmgmt.dto.OperRecordQueryDTO;
import com.zcunsoft.clklog.sysmgmt.models.request.OperRecordAddModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;

/**
 * 操作记录管理服务接口.
 */
public interface IOperRecordService {

    /**
     * 分页获取操作记录列表.
     *
     * @param query 查询请求
     * @return 分页获取操作记录列表的响应
     */
    ResponseBase<ListWithTotalCount<OperRecordDTO>> getOperRecordPageList(OperRecordQueryDTO query);

    /**
     * 添加操作记录.
     *
     * @param operrecord 操作记录
     */
    void add(OperRecordAddModel operrecord);
}
