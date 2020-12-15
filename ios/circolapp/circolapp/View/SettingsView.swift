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

import SwiftUI
import AppStorage
import Shared

struct SettingsView: View {
    @AppStorageCompat("school") var school = 0
    
    private let serverCompanion = ServerAPI.Companion()
    
    var body: some View {
        NavigationView {
            Form {
                Picker("School", selection: $school) {
                    ForEach(0..<Int(serverCompanion.numberOfServers)) { serverID in
                        let server = serverCompanion.getServer(serverID: Int32(serverID))
                        Text(serverCompanion.getServerName(server: server))
                    }
                }
                .onReceive([self.school].publisher.first()) { value in
                    UserDefaults.standard.set(value, forKey: "school")
                }
            }
            .navigationBarTitle("Settings", displayMode: .inline)
        }
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
