package kr.movements.smv2.service;

import kr.movements.smv2.dto.StatisticsAdminResponse;
import kr.movements.smv2.dto.StatisticsSiteResponse;

import java.time.LocalDate;

public interface StatisticsService {
    StatisticsAdminResponse adminStatistics(boolean totalSearch, LocalDate date);

    StatisticsSiteResponse siteStatistics(Long userId, boolean totalSearch, LocalDate date);
}
