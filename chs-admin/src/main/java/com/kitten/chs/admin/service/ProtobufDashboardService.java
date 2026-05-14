package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.DashboardStatsReqVO;
import com.kitten.chs.common.proto.DashboardStatsResponse;

public interface ProtobufDashboardService {

    DashboardStatsResponse getDashboardProtoStats(DashboardStatsReqVO reqVO);
}