package kr.movements.smv2.repository;

import kr.movements.smv2.dto.DriverListDto;
import kr.movements.smv2.dto.TransportNotPassListResponse;
import kr.movements.smv2.dto.WaybillDriverListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DriverMemberRepositoryCustom {
    Page<DriverListDto> findAllDriverList(String searchType, String keyword, Pageable pageable);
    Page<WaybillDriverListDto> findAllSearchDriverList(String searchType, String keyword, Pageable pageable);
    Page<TransportNotPassListResponse> findAllTransportFailureList(String searchType, String keyword, Pageable pageable);

}
