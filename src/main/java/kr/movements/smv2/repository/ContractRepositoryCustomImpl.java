package kr.movements.smv2.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static kr.movements.smv2.entity.QContractEntity.contractEntity;
import static kr.movements.smv2.entity.QSiteManagerEntity.siteManagerEntity;

@RequiredArgsConstructor
public class ContractRepositoryCustomImpl implements ContractRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DriverWaybillSearchStartSiteResponse> waybillSearchStartSite(String keyword) {
        return jpaQueryFactory.selectDistinct(new QDriverWaybillSearchStartSiteResponse(
                        contractEntity.startSiteId.id,
                        contractEntity.startSiteId.siteName,
                        contractEntity.startSiteId.latitude,
                        contractEntity.startSiteId.longitude
                ))
                .from(contractEntity)
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .where(
                        keywordContains(keyword),
                        contractEntity.hasDeleted.eq(false)
                )
                .fetch();
    }

    @Override
    public List<DriverWaybillSearchEndSiteResponse> waybillSearchEndSite(Long startSiteId) {
        return jpaQueryFactory.select(new QDriverWaybillSearchEndSiteResponse(
                        contractEntity.endSiteId.id,
                        contractEntity.endSiteId.siteName
                ))
                .from(contractEntity)
                .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                .where(
                        contractEntity.startSiteId.id.eq(startSiteId),
                        contractEntity.hasDeleted.eq(false)
                )
                .orderBy(contractEntity.endSiteId.siteName.asc())
                .fetch();
    }
/*
    @Override
    public List<ContractSearchSiteResponse> siteWaybillContractSite(Long siteId, String searchTypeValue) {
        if(searchTypeValue.equals("상차지")) {
            return jpaQueryFactory.select(new QContractSearchSiteResponse(
                            contractEntity.startSiteId.id,
                            contractEntity.startSiteId.siteName
                    ))
                    .from(contractEntity)
                    .join(siteManagerEntity).on(contractEntity.startSiteId.eq(siteManagerEntity))
                    .where(
                            contractEntity.startSiteId.id.eq(startSiteId),
                            contractEntity.hasDeleted.eq(false)
                    )
                    .orderBy(contractEntity.endSiteId.siteName.asc())
                    .fetch();
        } else {

        }

    }
 */

    private BooleanExpression keywordContains(String keyword) {
        if(StringUtils.hasText(keyword)) {
            return siteManagerEntity.siteName.contains(keyword);
        }else {
            return null;
        }
    }
}
