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

struct PadView: View {
    @State var screen: Screen? = .all
    @State var showOnboarding = !UserDefaults.standard.bool(forKey: "skipOnboarding")
    
    var body: some View {
        NavigationView {
            SidebarView(state: $screen)
        }
        .navigationViewStyle(DoubleColumnNavigationViewStyle())
        .sheet(isPresented: self.$showOnboarding, onDismiss: {
            UserDefaults.standard.set(true, forKey: "skipOnboarding")
        }) {
            OnboardingView()
        }
    }
}

struct PadView_Previews: PreviewProvider {
    static var previews: some View {
        PadView()
    }
}

enum Screen: Hashable {
    case all, favourites, reminders, settings
}
