package kr.movements.smv2.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.movements.smv2.dto.InquiryDto;
import kr.movements.smv2.dto.QInquiryDto;
import kr.movements.smv2.entity.QInquiryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class InquiryRepositoryCustomImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<InquiryDto> getAdminInquiryList(Pageable pageable) {
        QInquiryEntity inquiry = QInquiryEntity.inquiryEntity;

        List<InquiryDto> result = jpaQueryFactory
                .select(new QInquiryDto(
                        inquiry.id,
                        inquiry.inquiryCode,
                        inquiry.receiveEmail,
                        inquiry.inquiryText
                ))
                .from(inquiry)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(inquiry.count())
                .from(inquiry)
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<InquiryDto> getInquiryList(Long userId, Pageable pageable) {
        QInquiryEntity inquiry = QInquiryEntity.inquiryEntity;

        List<InquiryDto> query = jpaQueryFactory
                .select(new QInquiryDto(
                        inquiry.id,
                        inquiry.inquiryCode,
                        inquiry.receiveEmail,
                        inquiry.inquiryText
                ))
                .from(inquiry)
                .where(inquiry.userInfoId.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(inquiry.userInfoId.id.eq(userId))
                .fetchCount();

        return new PageImpl<>(query, pageable, total);
    }
}
