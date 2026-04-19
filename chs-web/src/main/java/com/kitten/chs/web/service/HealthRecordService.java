package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.rsp.FindHealthRecordListRespVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HealthRecordService {

    Response<FindHealthRecordListRespVO> findHealthRecordDetail(Long id);

    Response<List<FindHealthRecordListRespVO>> findMyHealthRecordList();

    void exportMyHealthRecord(Long id, HttpServletResponse response) throws Exception;

}
