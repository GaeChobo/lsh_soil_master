package kr.movements.smv2.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static kr.movements.smv2.entity.QDriverEntity.driverEntity;
import static kr.movements.smv2.entity.QUserInfoEntity.userInfoEntity;
import static kr.movements.smv2.entity.QWaybillEntity.waybillEntity;

@RequiredArgsConstructor
public class DriverMemberRepositoryCustomImpl implements DriverMemberRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<DriverListDto> findAllDriverList(String searchType, String keyword, Pageable pageable) {

        List<DriverListDto> driverListDtos = getDriverList(searchType, keyword, pageable);
        Long getCount = getAllDriverCount(searchType, keyword);

        return new PageImpl<>(driverListDtos, pageable, getCount);
    }

    @Override
    public Page<WaybillDriverListDto> findAllSearchDriverList(String searchType, String keyword, Pageable pageable) {
        List<WaybillDriverListDto> driverListDtos = getSearchDriverList(searchType, keyword, pageable);
        Long getCount = getAllDriverCount(searchType, keyword);

        return new PageImpl<>(driverListDtos, pageable, getCount);
    }

    @Override
    public Page<TransportNotPassListResponse> findAllTransportFailureList(String searchType, String keyword, Pageable pageable) {
        List<TransportNotPassListResponse> driverListDtos = getTransportFailureList(searchType, keyword, pageable);
        Long getCount = getTransportFailureCount(searchType, keyword);

        return new PageImpl<>(driverListDtos, pageable, getCount);
    }

    private List<DriverListDto> getDriverList(String searchType, String keyword, Pageable pageable) {
        List<DriverListDto> result = jpaQueryFactory.select(new QDriverListDto(
                        userInfoEntity.id,
                        driverEntity.id,
                        userInfoEntity.userName,
                        userInfoEntity.userPhone,
                        driverEntity.carNumber,
                        (jpaQueryFactory.select(waybillEntity.driver.id.count().as("unloadCnt")).from(waybillEntity).where(waybillEntity.driver.eq(driverEntity))),
                        (jpaQueryFactory.select(waybillEntity.driver.id.count().as("returnCnt")).from(waybillEntity).where(waybillEntity.driver.eq(driverEntity), waybillEntity.waybillStatusCode.eq("3060"))
                        )))
                .from(userInfoEntity)
                .join(driverEntity).on(userInfoEntity.eq(driverEntity.userInfoId))
                .where(
                        userInfoEntity.hasDeleted.eq(false),
                        searchTypeContent(searchType, keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(userInfoEntity.createdDate.desc().nullsLast())
                .fetch();
        return result;
    }
    private List<WaybillDriverListDto> getSearchDriverList(String searchType, String keyword, Pageable pageable) {
        List<WaybillDriverListDto> result = jpaQueryFactory.select(new QWaybillDriverListDto(
                        userInfoEntity.id,
                        driverEntity.id,
                        userInfoEntity.userName,
                        userInfoEntity.userPhone,
                        driverEntity.carNumber
                        ))
                .from(userInfoEntity)
                .join(driverEntity).on(userInfoEntity.eq(driverEntity.userInfoId))
                .where(
                        userInfoEntity.hasDeleted.eq(false),
                        searchTypeContent(searchType, keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(userInfoEntity.createdDate.desc().nullsLast())
                .fetch();
        return result;
    }

    private List<TransportNotPassListResponse> getTransportFailureList(String searchType, String keyword, Pageable pageable) {
        List<TransportNotPassListResponse> result = jpaQueryFactory.select(new QTransportNotPassListResponse(
                        driverEntity.userInfoId.id,
                        driverEntity.id,
                        driverEntity.userInfoId.userName,
                        driverEntity.userInfoId.userPhone,
                        driverEntity.carNumber,
                        (jpaQueryFactory.select(waybillEntity.driver.id.count().as("notPassCount")).from(waybillEntity).where(waybillEntity.driver.eq(driverEntity), waybillEntity.waybillStatusCode.eq(CommonCode.WAYBILL_STATUS_UNPASSED.getCode()))),
                        (jpaQueryFactory.select(waybillEntity.driver.id.count().as("totalPassCount")).from(waybillEntity).where(waybillEntity.driver.eq(driverEntity), (waybillEntity.waybillStatusCode.in(CommonCode.WAYBILL_STATUS_COMPLETE.getCode(),CommonCode.WAYBILL_STATUS_DELAY.getCode(),CommonCode.WAYBILL_STATUS_UNPASSED.getCode()))))
                ))
                .from(driverEntity)
                .join(userInfoEntity).on(userInfoEntity.eq(driverEntity.userInfoId))
                .where(
                        driverEntity.userInfoId.hasDeleted.eq(false),
                        searchTypeContent(searchType, keyword),
                        driverEntity.id.in(jpaQueryFactory.select(waybillEntity.driver.id).from(waybillEntity).where(waybillEntity.waybillStatusCode.eq("3060")))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(userInfoEntity.createdDate.desc().nullsLast())
                .fetch();
        return result;
    }
    private Long getAllDriverCount(String searchType, String content) {
        return jpaQueryFactory
                .select(userInfoEntity.count())
                .from(userInfoEntity)
                .join(driverEntity).on(userInfoEntity.eq(driverEntity.userInfoId))
                .where(
                        userInfoEntity.hasDeleted.eq(false),
                        searchTypeContent(searchType, content)
                ).fetchOne();
    }
    private Long getCount(String searchType, String content) {
        return jpaQueryFactory
                .select(userInfoEntity.count())
                .from(userInfoEntity)
                .where(
                        userInfoEntity.hasDeleted.eq(false),
                        searchTypeContent(searchType, content)
                ).fetchOne();
    }
    private Long getTransportFailureCount(String searchType, String content) {
        return jpaQueryFactory
                .select(driverEntity.userInfoId.count())
                .from(driverEntity)
                .join(userInfoEntity).on(userInfoEntity.eq(driverEntity.userInfoId))
                .where(
                        driverEntity.userInfoId.hasDeleted.eq(false),
                        searchTypeContent(searchType, content),
                        driverEntity.id.in(jpaQueryFactory.select(waybillEntity.driver.id).from(waybillEntity).where(waybillEntity.waybillStatusCode.eq("3060")))
                ).fetchOne();
    }

    private BooleanExpression searchTypeContent(String searchType, String content) {
        if(StringUtils.hasText(content)) {
            if (searchType == null) {
                return null;
            } else if (searchType.equals("회원명")) {
                return userInfoEntity.userName.contains(content);
            } else if (searchType.equals("차량번호")) {
                return driverEntity.carNumber.contains(content);
            } else if (searchType.equals("연락처")) {
                return userInfoEntity.userPhone.contains(content);
            } else {
                return null;
            }
        }else {
            return null;
        }
    }
}