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
    
    private let repository: CircularRepository
    private let key = "school"
    
    init(repository: CircularRepository) {
        self.repository = repository
        
        schoolID = UserDefaults.standard.integer(forKey: key)
        iOSRepository.updateCirculars(circularRepository: repository, circularUpdated: {})

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
    
    func updateCirculars(circularUpdated: @escaping () -> Void) {
        iOSRepository.updateCirculars(circularRepository: repository, circularUpdated: circularUpdated)
    }
    
    func startObservingCirculars() {
        stopObserving()
        circularWatcher = repository.circularDao.getCFlowCirculars(school: Int32(schoolID)).watch { circulars in
            self.circulars = circulars as! Array<Circular>;
        }
    }
    
    func stopObserving() {
        circularWatcher?.close()
    }
    
    func search(query: String) {
        let wrappedQuery = "%" + query + "%"
        
        stopObserving()
        circularWatcher = repository.circularDao.searchCircularsC(query: wrappedQuery, school: Int32(schoolID)).watch { circulars in
            self.circulars = circulars as! Array<Circular>;
        }
    }
}
