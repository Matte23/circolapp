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
import CoreSpotlight
import MobileCoreServices
import Shared

class iOSRepository {
    static let database = DatabaseFactory().createDatabase(sqlDriver: DatabaseDriverFactory().createDriver())
    
    public static func getCircularDao() -> CircularDao {
        return CircularDao(database: database)
    }
    
    public static func getCircularRepository() -> CircularRepository {
        let serverAPI = iOSServerApi.instance.serverAPI
        return CircularRepository(circularDao: getCircularDao(), serverAPI: serverAPI)
    }
    
    public static func updateCirculars(circularRepository: CircularRepository) {
        circularRepository.updateCirculars(returnNewCirculars: true, completionHandler:
                                            { result, error in
                                                if let errorReal = error {
                                                    print(errorReal.localizedDescription)
                                                    return
                                                }
                                                
                                                // Database was resetted, remove all circulars from spotlight
                                                if result?.second == 1 {
                                                    deleteAllFromSpotlight(reindex: false, serverID: -1)
                                                }
                                                
                                                // Index circulars
                                                for circular in result!.first as! Array<Circular> {
                                                    indexToSpotlight(circular: circular)
                                                }
                                            })
    }
    
    public static func indexAllToSpotlight(serverID: Int) {
        let circulars = getCircularDao().getCirculars(school: Int32(serverID))
        
        for circular in circulars {
            indexToSpotlight(circular: circular)
        }
    }
    
    public static func indexToSpotlight(circular: Circular) {
        let attributeSet = CSSearchableItemAttributeSet(itemContentType: kUTTypeText as String)
        attributeSet.title = "Circular number \(circular.id)"
        attributeSet.contentDescription = circular.name
        attributeSet.identifier = "\(circular.id)"
        
        let item = CSSearchableItem(uniqueIdentifier: "\(circular.id)", domainIdentifier: "net.underdesk.circolapp", attributeSet: attributeSet)
        item.expirationDate = Date.distantFuture
        
        CSSearchableIndex.default().indexSearchableItems([item]) { error in
            if let error = error {
                print("Indexing error: \(error.localizedDescription)")
            }
        }
    }
    
    public static func deleteAllFromSpotlight(reindex: Bool, serverID: Int) {
        CSSearchableIndex.default().deleteAllSearchableItems(completionHandler: {
            error in
            if let errorReal = error {
                print(errorReal.localizedDescription)
                return
            }
            
            if reindex {
                DispatchQueue.main.async {
                    indexAllToSpotlight(serverID: serverID)
                }
            }
        })
    }
}
