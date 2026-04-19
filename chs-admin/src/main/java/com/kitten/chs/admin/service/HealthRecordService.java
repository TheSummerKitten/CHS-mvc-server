package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.CreateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindHealthRecordListRespVO;
import com.kitten.chs.common.excel.HealthRecordExportData;
import com.kitten.chs.common.utils.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HealthRecordService {

    Response<?> createHealthRecord(CreateHealthRecordReqVO reqVO);

    Response<?> updateHealthRecord(UpdateHealthRecordReqVO reqVO);

    Response<?> deleteHealthRecord(Long id);

    Response<List<FindHealthRecordListRespVO>> findHealthRecordList(Long userId);

    Response<FindHealthRecordListRespVO> findHealthRecordDetail(Long id);

    void exportHealthRecord(Long id, HttpServletResponse response) throws Exception;

}
