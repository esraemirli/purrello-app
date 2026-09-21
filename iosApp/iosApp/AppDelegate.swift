import UIKit
import UserNotifications
import Shared

/// Build configuration, injected through Configuration/Config.xcconfig → Info.plist.
/// The Android counterpart is BuildConfig; neither hardcodes values in source.
enum AppEnvironment {
    static var environment: String { string("PurrelloEnvironment") ?? "DEV" }
    static var baseUrl: String { string("PurrelloBaseUrl") ?? "" }
    static var useFakeApi: Bool { (string("PurrelloUseFakeApi") ?? "NO") == "YES" }
    static var appVersion: String { string("CFBundleShortVersionString") ?? "0" }

    static var isDebug: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }

    private static func string(_ key: String) -> String? {
        (Bundle.main.object(forInfoDictionaryKey: key) as? String)?
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .nilIfEmpty
    }
}

private extension String {
    var nilIfEmpty: String? { isEmpty ? nil : self }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        MainViewControllerKt.startPurrelloIos(
            environment: AppEnvironment.environment,
            baseUrl: AppEnvironment.baseUrl,
            useFakeApi: AppEnvironment.useFakeApi,
            isDebug: AppEnvironment.isDebug,
            appVersion: AppEnvironment.appVersion
        )

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
