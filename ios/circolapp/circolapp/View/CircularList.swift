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
import SwiftUIRefresh

struct CircularList: View {
    @ObservedObject var circularViewModel = CircularViewModel(repository: iOSRepository.getCircularRepository())
    @ObservedObject var searchBar: SearchBar = SearchBar(placeholder: "Search circulars")
    
    var body: some View {
        List(circularViewModel.circulars, id: \.id) { circular in
            CircularView(circular: circular, idIsHumanReadable: circularViewModel.idsAreHumanReadable)
        }
        .navigationBarTitle(Text("Circulars"), displayMode: .large)
        .pullToRefresh() { endRefreshing in circularViewModel.updateCirculars {
                DispatchQueue.main.async {
                    endRefreshing()
                }
            }
        }
        .addSearchBar(self.searchBar)
        .onReceive(searchBar.$text) {query in
            self.circularViewModel.search(query: query)
        }
        .onAppear {
            self.circularViewModel.startObservingCirculars()
        }
        .onDisappear(perform: {
            self.circularViewModel.stopObserving()
        })
    }
}

struct CircularList_Previews: PreviewProvider {
    static var previews: some View {
        CircularList()
    }
}
