package com.jangsacartel.biz.notification.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.jangsacartel.biz.notification.dto.NotificationDTO;

public interface NotificationMapper {

    void insertNotification(NotificationDTO dto);

    List<NotificationDTO> findPending(
        @Param("receiverUserId") int receiverUserId,
        @Param("limit") int limit
    );

    int deleteById(@Param("notificationId") long notificationId);

    int deleteBatch(@Param("ids") List<Long> ids);

    List<NotificationDTO> findByReceiver(
        @Param("receiverUserId") int receiverUserId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    int countUnread(@Param("receiverUserId") int receiverUserId);

    int markRead(
        @Param("notificationId") long notificationId,
        @Param("receiverUserId") int receiverUserId
    );

    int markAllRead(@Param("receiverUserId") int receiverUserId);

    int deleteRead(@Param("receiverUserId") int receiverUserId);
    
    List<NotificationDTO> findUnreadAfterId(
    	    @Param("receiverUserId") int receiverUserId,
    	    @Param("afterNotificationId") Long afterNotificationId,
    	    @Param("limit") int limit
    	);

	int deleteReadOlderThanDays(@Param("days") int days);

}
