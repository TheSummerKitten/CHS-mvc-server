package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.CreateMedicationReminderReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateMedicationReminderReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindMedicationReminderListRespVO;
import com.kitten.chs.common.utils.Response;

import java.util.List;

public interface MedicationReminderService {

    Response<?> createMedicationReminder(CreateMedicationReminderReqVO reqVO);

    Response<?> updateMedicationReminder(UpdateMedicationReminderReqVO reqVO);

    Response<?> deleteMedicationReminder(Long id);

    Response<List<FindMedicationReminderListRespVO>> findMedicationReminderList(Long healthRecordId);

}
