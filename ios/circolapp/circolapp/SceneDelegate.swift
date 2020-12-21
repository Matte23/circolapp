//
//  SceneDelegate.swift
//  circolapp
//
//  Created by Matteo Schiff on 01/12/20.
//

import UIKit
import CoreSpotlight
import SwiftUI

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        // Create the SwiftUI view that provides the window contents.
        let contentView = ContentView()
        
        // Use a UIHostingController as window root view controller.
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = UIHostingController(rootView: contentView)
            self.window = window
            window.makeKeyAndVisible()
        }
        
        if let userActivity = connectionOptions.userActivities.first {
            handleUserActivity(userActivity: userActivity)
        }
    }
    
    func scene(_ scene: UIScene, continue userActivity: NSUserActivity) {
        // Called when the user open an entry from Spotlight
        handleUserActivity(userActivity: userActivity)
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // This occurs shortly after the scene enters the background, or when its session is discarded.
        // Release any resources associated with this scene that can be re-created the next time the scene connects.
        // The scene may re-connect later, as its session was not neccessarily discarded (see `application:didDiscardSceneSessions` instead).
    }
    
    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
        // Use this method to restart any tasks that were paused (or not yet started) when the scene was inactive.
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
        // This may occur due to temporary interruptions (ex. an incoming phone call).
    }
    
    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
        // Use this method to undo the changes made on entering the background.
        
        let schoolID = UserDefaults.standard.integer(forKey: "school")
        let reminders = iOSRepository.getCircularDao().getReminders(school: Int32(schoolID))
        
        // Remove reminder flag if notification was delivered
        UNUserNotificationCenter.current().getPendingNotificationRequests { notifications in
            // This function has to be called from the main thread because it is where database is accessible
            DispatchQueue.main.async {
                loop:
                for circular in reminders {
                    for notification in notifications {
                        if (String(circular.id) == notification.identifier) {
                            continue loop
                        }
                    }
                    
                    iOSRepository.getCircularDao().update(id: circular.id, school: circular.school, favourite: circular.favourite, reminder: false, completionHandler: {_,_ in })
                }
            }
        }
    }
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // to restore the scene back to its current state.
    }
    
    func handleUserActivity(userActivity: NSUserActivity) {
        if userActivity.activityType == CSSearchableItemActionType {
            if let uniqueIdentifier = userActivity.userInfo?[CSSearchableItemActivityIdentifier] as? String {
                guard let circularID = Int64(uniqueIdentifier) else { return }
                let schoolID = UserDefaults.standard.integer(forKey: "school")
                
                let circularDao = iOSRepository.getCircularDao()
                let circular = circularDao.getCircular(id: circularID, school: Int32(schoolID))
                
                URLUtils.openUrl(url: circular.url)
            }
        }
    }
}
