package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.assemblers.OperRecordAssembler;
import com.zcunsoft.clklog.sysmgmt.domains.OperRecords;
import com.zcunsoft.clklog.sysmgmt.dto.ListWithTotalCount;
import com.zcunsoft.clklog.sysmgmt.dto.OperRecordDTO;
import com.zcunsoft.clklog.sysmgmt.dto.OperRecordQueryDTO;
import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import com.zcunsoft.clklog.sysmgmt.models.request.OperRecordAddModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.repository.IOperRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperRecordServiceImpl implements IOperRecordService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ICodeService codeService;

    private final IOperRecordRepository operRecordRepository;

    public OperRecordServiceImpl(ICodeService codeService, IOperRecordRepository operRecordRepository) {
        this.codeService = codeService;
        this.operRecordRepository = operRecordRepository;
    }

    @Override
    public ResponseBase<ListWithTotalCount<OperRecordDTO>> getOperRecordPageList(OperRecordQueryDTO queryReq) {
        ErrorCode code = ErrorCode.Success;
        String msg = codeService.getMessage(code);

        ListWithTotalCount<OperRecordDTO> list = null;
        try {
            Pageable pageable = PageRequest.of(queryReq.getPageIndex() - 1, queryReq.getPageSize(),
                    Sort.by(Direction.DESC, "id"));

            Specification<OperRecords> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!queryReq.getUser().isEmpty()) {
                    predicates.add(cb.equal(root.get("user"), queryReq.getUser()));
                }
                if (queryReq.getStartTime() != null && !queryReq.getStartTime().isEmpty()) {
                    Timestamp m_startTime = Timestamp
                            .valueOf(queryReq.getStartTime().replace("T", " ").replace("Z", ""));
                    predicates.add(cb.greaterThan(root.get("opertime"), m_startTime));
                }
                if (queryReq.getEndTime() != null && !queryReq.getEndTime().isEmpty()) {
                    Timestamp m_endTime = Timestamp.valueOf(queryReq.getEndTime().replace("T", " ").replace("Z", ""));
                    predicates.add(cb.lessThan(root.get("opertime"), m_endTime));
                }
                if (!predicates.isEmpty()) {
                    Predicate[] pre = new Predicate[predicates.size()];
                    return cb.and(predicates.toArray(pre));
                } else {
                    return null;
                }
            };

            Page<OperRecords> pageresult = operRecordRepository.findAll(spec, pageable);
            List<OperRecordDTO> dtoList = (new OperRecordAssembler()).toOperRecordDTOList(pageresult.getContent());

            list = new ListWithTotalCount<OperRecordDTO>(dtoList, (int) pageresult.getTotalElements());

        } catch (Exception e) {
            logger.error("", e);
            code = ErrorCode.Failed;
            msg = e.getMessage();
        }
        ResponseBase<ListWithTotalCount<OperRecordDTO>> resp = new ResponseBase<ListWithTotalCount<OperRecordDTO>>(code,
                msg, list);
        return resp;
    }

    @Override
    public void add(OperRecordAddModel operrecord) {
        try {
            OperRecords m_operrecord = new OperRecords();
            m_operrecord.setAction(operrecord.getAction());
            m_operrecord.setOpertime(operrecord.getOpertime());
            m_operrecord.setUser(operrecord.getUser());
            this.operRecordRepository.save(m_operrecord);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
