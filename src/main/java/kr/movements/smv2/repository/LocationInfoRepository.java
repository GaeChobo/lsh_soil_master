package kr.movements.smv2.repository;

import kr.movements.smv2.dto.TransportRealTimeInfoDto;
import kr.movements.smv2.dto.waybillLocationDto;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.LocationInfoEntity;
import kr.movements.smv2.entity.WaybillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationInfoRepository extends JpaRepository<LocationInfoEntity, Long> {

//    List<TransportRealTimeInfoDto> findAllByWaybillOrderByCreatedDate(WaybillEntity waybillEntity);

    boolean existsByWaybillAndHasDeleted(WaybillEntity waybill, Boolean hasDeleted);

    @Query(value = "select " +
            "new kr.movements.smv2.dto.TransportRealTimeInfoDto(locationInfo.lat, locationInfo.lon) " +
            "from LocationInfoEntity locationInfo " +
            "where locationInfo.waybill.id = :waybillId and locationInfo.hasDeleted = false order by locationInfo.createdDate asc")
    Optional<List<TransportRealTimeInfoDto>> findWaybillLocations(@Param("waybillId") Long waybillId);

    @Query(value = "SELECT lat, lon\n" +
            "FROM location_info\n" +
            "WHERE waybill_id = :waybillId and has_deleted = false\n" +
            "ORDER BY created_date DESC\n" +
            "LIMIT 1;", nativeQuery = true)
    waybillLocationDto findByWaybillIdLocation(@Param("waybillId") Long waybillId);

}

