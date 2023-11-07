package kr.movements.smv2.service;

import kr.movements.smv2.dto.NotifiesResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.management.Notification;
import java.util.List;

public interface NotifyService {
    void sendAlarmNotification(String message);
    void addEmitter(SseEmitter emitter);

    Flux<Notification> streamNotifications();
    void sendNotification(Notification notification);


    List<NotifiesResponse> siteManagerNotifies(Long userId);
    void readNotify(Long notifyId);

}
