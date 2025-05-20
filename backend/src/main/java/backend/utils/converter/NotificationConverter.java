package backend.utils.converter;

import backend.dto.response.NotificationResponse;
import backend.model.Notification;

public class NotificationConverter {

	public static NotificationResponse convertToDto(Notification n) {
		NotificationResponse d = new NotificationResponse();
		d.setNotificationId(n.getNotificationId());
		d.setCommunityId(n.getCommunityId());
		d.setMessage(n.getMessage());
		d.setCreatedAt(n.getTimestamp());
		d.setRead(n.isReadStatus());
		return d;
	}

}
