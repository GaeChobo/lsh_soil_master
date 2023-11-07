

package kr.movements.smv2.service;

import kr.movements.smv2.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpSession;

public interface DriverMemberService {

//    void driverSave(DriverSaveDto dto);

    Page<DriverListDto> driverList(String searchType, String keyword, Pageable pageable);

    DriverInfoDto driverInfo(Long userId);

    void driverUpdate(Long userId, DriverUpdateDto dto);

    void driverAppDelete(Long userId, String userName);

    void driverDelete(Long userId);

    Page<WaybillDriverListDto> waybillDriverList(String searchType, String keyword, Pageable pageable);
    Page<TransportNotPassListResponse> transportFailureList(String searchType, String keyword, Pageable pageable);

    void driverMemberSave(DriverMemberSaveDto dto);

    void sendSms(SmsDto smsDto);
    void receiveSms(SmsReceiveDto smsReceiveDto);
}
