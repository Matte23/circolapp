/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import Foundation
import Firebase
import Shared

class iOSServerApi {
    static let instance = iOSServerApi()
    
    private let schoolKey = "school"
    private let topicKey = "topic"
    private let serverCompanion = ServerAPI.Companion()
    
    var serverAPI: ServerAPI
    private var userDefaultsObserver: NSKeyValueObservation? = nil
    
    init() {
        let serverID = UserDefaults.standard.integer(forKey: schoolKey)
        let server = serverCompanion.getServer(serverID: Int32(serverID))
        serverAPI = ServerAPI(serverName: server)
        
        userDefaultsObserver = UserDefaults.standard.observe(\.school, options: [.initial, .new], changeHandler: { (defaults, change) in
            self.changeServer(serverID: change.newValue ?? 0)
        })
    }
    
    deinit {
        userDefaultsObserver?.invalidate()
    }
    
    func changeServer(serverID: Int) {
        // Change server provider
        let serverID = UserDefaults.standard.integer(forKey: schoolKey)
        let server = serverCompanion.getServer(serverID: Int32(serverID))
        serverAPI = ServerAPI(serverName: server)
        
        // Reset spotlight indexed items
        iOSRepository.deleteAllFromSpotlight(reindex: true, serverID: serverID)
        
        // Change FCM topic
        let nullableOldTopic = UserDefaults.standard.string(forKey: topicKey)
        let newTopic = serverCompanion.getServerTopic(serverID: Int32(serverID))
        if (nullableOldTopic == nil || nullableOldTopic != newTopic) {
            if let oldTopic = nullableOldTopic {
                Messaging.messaging().unsubscribe(fromTopic: oldTopic + "IOS")
            }

            Messaging.messaging().subscribe(toTopic: newTopic + "IOS") { error in
              print("Subscribed to topic: " + newTopic)
            }
            
            UserDefaults.standard.set(newTopic, forKey: topicKey)
        }
        
        iOSRepository.updateCirculars(circularRepository: CircularRepository(circularDao: iOSRepository.getCircularDao(), serverAPI: serverAPI))
    }
}
