package gitflow.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class NotifyUtil
{
    private static final String ERRORS_GROUP = "Gitflow Errors";
    private static final String NOTIFICATIONS_GROUP = "Gitflow Notifications";

    public static void notifySuccess(Project project, String title, String message) {
        notify(NotificationType.INFORMATION, NOTIFICATIONS_GROUP, project, title, message);
    }

	public static void notifyInfo(Project project, String title, String message) {
		notify(NotificationType.INFORMATION, ERRORS_GROUP, project, title, message);
	}

    public static void notifyError(Project project, String title, String message) {
        notify(NotificationType.ERROR, ERRORS_GROUP, project, title, message);
    }

    public static void notifyError(Project project, String title, Exception exception) {
        notify(NotificationType.ERROR, ERRORS_GROUP, project, title, exception.getMessage());
    }

    private static void notify(NotificationType type, String groupId, Project project, String title, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(groupId)
                .createNotification(title, message, type)
                .notify(project);
    }
}
