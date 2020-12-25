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

struct SidebarView: View {
    @Binding var state: Screen?
    
    var body: some View {
        if #available(iOS 14.0, *) {
            List(selection: $state) {
                NavigationLink(
                    destination: CircularList(),
                    tag: Screen.all,
                    selection: $state,
                    label: {
                        Label("All circulars", systemImage: "folder" )
                    })
                NavigationLink(
                    destination: FavouritesList(),
                    tag: Screen.favourites,
                    selection: $state,
                    label: {
                        Label("Bookmarks", systemImage: "book")
                    })
                NavigationLink(
                    destination: RemindersList(),
                    tag: Screen.reminders,
                    selection: $state,
                    label: {
                        Label("Reminders", systemImage: "alarm")
                    })
                NavigationLink(
                    destination: SettingsView(),
                    tag: Screen.settings,
                    selection: $state,
                    label: {
                        Label("Settings", systemImage: "gear")
                    })
            }
            .listStyle(SidebarListStyle())
            .navigationTitle("CircolApp")
        } else {
            // Fallback on earlier versions
        }
    }
}
