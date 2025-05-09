package com.zcunsoft.clklog.sysmgmt.assemblers;

import com.zcunsoft.clklog.sysmgmt.domains.OperRecords;
import com.zcunsoft.clklog.sysmgmt.dto.OperRecordDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * OperRecord装配工具
 */
public class OperRecordAssembler {
    public OperRecordDTO toOperRecordDTO(OperRecords operRecord) {

        OperRecordDTO operRecordDto = new OperRecordDTO();
        operRecordDto.setAction(operRecord.getAction());
        operRecordDto.setUser(operRecord.getUser());
        operRecordDto.setId(operRecord.getId());
        operRecordDto.setOpertime(operRecord.getOpertime());
        return operRecordDto;
    }

    public List<OperRecordDTO> toOperRecordDTOList(List<OperRecords> activities) {
        final List<OperRecordDTO> dtolList = new ArrayList<OperRecordDTO>(activities.size());
        for (OperRecords operRecord : activities) {
            dtolList.add(toOperRecordDTO(operRecord));
        }
        return dtolList;
    }
}
