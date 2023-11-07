package kr.movements.smv2.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.dto.NotifiesResponse;
import kr.movements.smv2.dto.QDriverWaybillSearchEndSiteResponse;
import kr.movements.smv2.dto.QNotifiesResponse;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static kr.movements.smv2.entity.QAlarmEntity.alarmEntity;
import static kr.movements.smv2.entity.QContractEntity.contractEntity;
import static kr.movements.smv2.entity.QSiteManagerEntity.siteManagerEntity;
import static kr.movements.smv2.entity.QWaybillEntity.waybillEntity;

@RequiredArgsConstructor
public class AlarmRepositoryCustomImpl implements AlarmRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<NotifiesResponse> findAllNotifies(Long siteId, String siteType) {
        return jpaQueryFactory.select(new QNotifiesResponse(
                        alarmEntity.id,
                        alarmEntity.waybill.endSiteName,
                        alarmEntity.waybill.id,
                        alarmEntity.waybill.waybillNum
                ))
                .from(alarmEntity)
                .join(waybillEntity).on(alarmEntity.waybill.eq(waybillEntity))
                .join(contractEntity).on(waybillEntity.contract.eq(contractEntity))
                .where(
                        siteType(siteId, siteType),
                        alarmEntity.alarmCheck.eq(false)
                )
                .orderBy(alarmEntity.createdDate.desc())
                .fetch();
    }

    private BooleanExpression searchToday(LocalDate date){
        return waybillEntity.departureTime.between(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.MAX));
    }
    private BooleanExpression siteType(Long siteId, String siteType){
        if(siteType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) { //상차지
            return contractEntity.startSiteId.id.eq(siteId);
        }else if(siteType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {
            return contractEntity.endSiteId.id.eq(siteId);
        }
        return null;
    }
}
