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
import Shared

class CircularViewModel: ObservableObject {
    @Published var circulars = Array<Circular>()
    
    private var circularWatcher: Ktor_ioCloseable? = nil
    private var userDefaultsObserver: NSKeyValueObservation? = nil
    private var schoolID = 0
    private var category: Category = .all
    
    private let repository: CircularRepository
    private let key = "school"
    
    init(repository: CircularRepository) {
        self.repository = repository
        
        schoolID = UserDefaults.standard.integer(forKey: key)
        updateCirculars()
        
        userDefaultsObserver = UserDefaults.standard.observe(\.school, options: [.initial, .new], changeHandler: { (defaults, change) in
            self.schoolID = change.newValue ?? 0
            
            if (self.circularWatcher != nil) {
                self.startObservingCirculars()
            }
        })
    }
    
    deinit {
        userDefaultsObserver?.invalidate()
    }
    
    func updateCirculars() {
        self.repository.updateCirculars(returnNewCirculars: false, completionHandler:
                                            { result, error in
                                                if let errorReal = error {
                                                    print(errorReal.localizedDescription)
                                                }
                                            })
    }
    
    func startObservingCirculars() {
        category = .all
        stopObserving()
        circularWatcher = repository.circularDao.getCFlowCirculars(school: Int32(schoolID)).watch { circulars in
            self.circulars = circulars as! Array<Circular>;
        }
    }
    
    func startObservingFavourites() {
        category = .favourites
        stopObserving()
        circularWatcher = repository.circularDao.getFavouritesC(school: Int32(schoolID)).watch { circulars in
            self.circulars = circulars as! Array<Circular>;
        }
    }
    
    func startObservingReminders() {
        category = .reminders
        stopObserving()
        circularWatcher = repository.circularDao.getCFlowReminders(school: Int32(schoolID)).watch { circulars in
            self.circulars = circulars as! Array<Circular>;
        }
    }
    
    func stopObserving() {
        circularWatcher?.close()
    }
    
    func search(query: String) {
        let wrappedQuery = "%" + query + "%"
        
        stopObserving()
        switch category {
        case .all:
            circularWatcher = repository.circularDao.searchCircularsC(query: wrappedQuery, school: Int32(schoolID)).watch { circulars in
                self.circulars = circulars as! Array<Circular>;
            }
        case .favourites:
            circularWatcher = repository.circularDao.searchFavouritesC(query: wrappedQuery, school: Int32(schoolID)).watch { circulars in
                self.circulars = circulars as! Array<Circular>;
            }
        case .reminders:
            circularWatcher = repository.circularDao.searchRemindersC(query: wrappedQuery, school: Int32(schoolID)).watch { circulars in
                self.circulars = circulars as! Array<Circular>;
            }
        }
    }
}

enum Category {
    case all, favourites, reminders
}
