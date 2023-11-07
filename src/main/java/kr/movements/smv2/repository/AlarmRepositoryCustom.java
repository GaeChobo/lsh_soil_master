package kr.movements.smv2.repository;

import kr.movements.smv2.dto.NotifiesResponse;

import java.util.List;

public interface AlarmRepositoryCustom {
    List<NotifiesResponse> findAllNotifies(Long siteId, String siteType);
}
