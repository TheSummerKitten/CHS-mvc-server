package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.DashboardStatsReqVO;
import com.kitten.chs.admin.model.vo.rsp.DashboardStatsRespVO;

public interface DashboardService {

    DashboardStatsRespVO getDashboardStats(DashboardStatsReqVO reqVO);
}
