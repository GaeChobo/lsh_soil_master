package kr.movements.smv2.repository;

import kr.movements.smv2.dto.SearchAddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SiteManagerRepositoryCustom {
    Page<SearchAddressResponse> findAllSearchAddress(Long siteId, String searchType, String keyword, Pageable pageable);
}
