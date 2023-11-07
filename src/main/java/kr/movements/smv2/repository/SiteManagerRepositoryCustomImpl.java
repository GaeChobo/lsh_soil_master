package kr.movements.smv2.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.dto.QSearchAddressResponse;
import kr.movements.smv2.dto.SearchAddressResponse;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static kr.movements.smv2.entity.QContractEntity.contractEntity;
import static kr.movements.smv2.entity.QSiteManagerEntity.siteManagerEntity;
import static kr.movements.smv2.entity.QUserInfoEntity.userInfoEntity;
import static kr.movements.smv2.entity.QWaybillEntity.waybillEntity;

@RequiredArgsConstructor
public class SiteManagerRepositoryCustomImpl implements SiteManagerRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<SearchAddressResponse> findAllSearchAddress(Long siteId, String searchType, String keyword, Pageable pageable) {
        List<SearchAddressResponse> searchAddressList = getSearchAddress(siteId, searchType, keyword, pageable);
        Long getCount = getSearchAddressCount(siteId, searchType, keyword);

        return new PageImpl<>(searchAddressList, pageable, getCount);
    }

    private List<SearchAddressResponse> getSearchAddress(Long siteId, String searchType, String keyword, Pageable pageable) {
        //상차지 계정이면 searchType은 하차지(2020)
        return jpaQueryFactory.select(new QSearchAddressResponse(
                        siteManagerEntity.id,
                        siteManagerEntity.siteName,
                        siteManagerEntity.address,
                        siteManagerEntity.roadAddress,
                        siteManagerEntity.addressDetail,
                        siteManagerEntity.zipCode

                ))
                .from(siteManagerEntity)
                .where(
                        siteManagerEntity.hasDeleted.eq(false),
                        searchAddress(keyword),
                        searchContractSite(siteId, searchType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
//                .orderBy(waybillEntity.createdDate.desc().nullsLast())
                .fetch();
    }

    private Long getSearchAddressCount(Long siteId, String searchType, String keyword) {

        return jpaQueryFactory
                .select(siteManagerEntity.count())
                .from(siteManagerEntity)
                .where(
                        siteManagerEntity.hasDeleted.eq(false),
                        searchAddress(keyword),
                        searchContractSite(siteId, searchType)
                ).fetchOne();
    }


    /**
     * 주소 검색
     * @param keyword
     * @return
     */
    private BooleanExpression searchAddress(String keyword) {
        if(StringUtils.hasText(keyword)) {
            return siteManagerEntity.address.contains(keyword).or(siteManagerEntity.roadAddress.contains(keyword));
        }else {
            return null;
        }
    }

    /**
     * 내 siteId와 검색하려는 현장 타입
     * @param siteId
     * @param searchSiteType
     * @return
     */
    private BooleanExpression searchContractSite(Long siteId, String searchSiteType) {
        if(searchSiteType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {
            return siteManagerEntity.id.in(jpaQueryFactory.select(contractEntity.endSiteId.id).from(contractEntity).where(contractEntity.startSiteId.id.eq(siteId)));
        }else {
            return siteManagerEntity.id.in(jpaQueryFactory.select(contractEntity.startSiteId.id).from(contractEntity).where(contractEntity.endSiteId.id.eq(siteId)));
        }
    }
}
