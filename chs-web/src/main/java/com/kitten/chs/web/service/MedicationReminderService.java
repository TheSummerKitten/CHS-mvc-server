package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateMedicationReminderReqVO;
import com.kitten.chs.web.model.req.UpdateMedicationReminderReqVO;
import com.kitten.chs.web.model.rsp.FindMedicationReminderListRespVO;

import java.util.List;

public interface MedicationReminderService {
    Response<?> createMedicationReminder(CreateMedicationReminderReqVO reqVO);

    Response<?> updateMedicationReminder(UpdateMedicationReminderReqVO reqVO);

    Response<?> deleteMedicationReminder(Long id);

    Response<List<FindMedicationReminderListRespVO>> findMedicationReminderList(Long healthRecordId);

    Response<List<FindMedicationReminderListRespVO>> findMyMedicationReminderList();

}
