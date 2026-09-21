import UIKit
import UserNotifications
import Shared

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // TODO(env): read these from the xcconfig per scheme (Debug → DEV + fake API, Release → PROD).
        #if DEBUG
        MainViewControllerKt.startPurrelloIos(
            environment: "DEV",
            baseUrl: "https://api.dev.purrello.app/",
            useFakeApi: true,
            isDebug: true,
            appVersion: Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0"
        )
        #else
        MainViewControllerKt.startPurrelloIos(
            environment: "PROD",
            baseUrl: "https://api.purrello.app/",
            useFakeApi: false,
            isDebug: false,
            appVersion: Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0"
        )
        #endif

        UNUserNotificationCenter.current().delegate = self
        // TODO(push): request authorization + FirebaseMessaging/APNs token → PUT /v1/devices/{deviceId} (docs/api/common/push.md).
        return true
    }

    // Notification tapped → pending deep link (applied once the user is in Main).
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        MainViewControllerKt.onPushNotificationOpened(userInfo: response.notification.request.content.userInfo)
        completionHandler()
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .badge, .sound])
    }
}
