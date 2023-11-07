package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.dto.NotifiesResponse;
import kr.movements.smv2.entity.AlarmEntity;
import kr.movements.smv2.entity.SiteManagerEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.repository.AlarmRepository;
import kr.movements.smv2.repository.SiteManagerRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.management.Notification;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
    private final AlarmRepository alarmRepository;
    private final SiteManagerRepository siteManagerRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public void sendAlarmNotification(String message) {

    }

    @Override
    public void addEmitter(SseEmitter emitter) {

    }

    @Override
    public Flux<Notification> streamNotifications() {
        return null;
    }

    @Override
    public void sendNotification(Notification notification) {

    }

    @Override
    public List<NotifiesResponse> siteManagerNotifies(Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, false).orElseThrow(() -> new BadRequestException("사용자 정보를 확인 할 수 없습니다"));
        SiteManagerEntity siteManagerEntity = siteManagerRepository.findByUserIdAndHasDeleted(userInfoEntity, false).orElseThrow(() -> new BadRequestException("현장관리자 정보를 확인 할 수 없습니다"));
        String siteType = siteManagerEntity.getSiteTypeCode();

        return alarmRepository.findAllNotifies(siteManagerEntity.getId(), siteType);
    }

    @Override
    @Transactional
    public void readNotify(Long notifyId) {
        AlarmEntity alarmEntity = alarmRepository.findById(notifyId).orElseThrow(() -> new BadRequestException("알림을 확인 할 수 없습니다"));
        alarmEntity.update(true);
    }

}
