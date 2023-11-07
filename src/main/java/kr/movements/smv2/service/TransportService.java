package kr.movements.smv2.service;

import kr.movements.smv2.dto.TransportDailyNotPassListResponse;
import kr.movements.smv2.dto.TransportRealTimeInfoResponse;
import kr.movements.smv2.dto.TransportRealTimeListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransportService {
    TransportRealTimeListResponse transportRealTimeList(Long userId, String searchType, String keyword, Pageable pageable);
    TransportRealTimeInfoResponse transportRealTimeInfo(Long waybillId);
    Page<TransportDailyNotPassListResponse> transportDailyNotPassList(Long userId, Long siteManagerId, String siteTypeCode, String searchType, String keyword,Pageable pageable);

}
