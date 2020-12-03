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
import UIKit
import Shared

struct ContentView: View {
    @ObservedObject var circularViewModel = CircularViewModel(repository: iOSRepository.getCircularRepository())
    
    var body: some View {
        NavigationView {
            List(circularViewModel.circulars, id: \.id) { circular in
                CircularView(circular: circular)
            }
            .navigationBarTitle(Text("Circulars"), displayMode: .large)
            .onAppear {
                self.circularViewModel.startObservingCirculars()
            }
            .onDisappear(perform: {
                self.circularViewModel.stopObserving()
            })
        }
        
        Divider()
        HStack(spacing: 50) {
            Button(action: {
                self.circularViewModel.startObservingCirculars()
            }) {
                Image(systemName: "folder")
            }
            
            Button(action: {
                self.circularViewModel.startObservingFavourites()
            }) {
                Image(systemName: "book")
            }
            
            Button(action: {
                self.circularViewModel.startObservingAlarms()
            }) {
                Image(systemName: "alarm")
            }
        }.frame(height: 50.0)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
