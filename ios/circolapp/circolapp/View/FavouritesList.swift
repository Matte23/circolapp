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

struct FavouritesList: View {
    @ObservedObject var favouritesViewModel = FavouritesViewModel(repository: iOSRepository.getCircularRepository())
    @ObservedObject var searchBar: SearchBar = SearchBar(placeholder: "Search circulars")
    
    var body: some View {
        NavigationView {
            List(favouritesViewModel.circulars, id: \.id) { circular in
                CircularView(circular: circular)
            }
            .navigationBarTitle(Text("Favourites"), displayMode: .large)
            .addSearchBar(self.searchBar)
            .onReceive(searchBar.$text) {query in
                self.favouritesViewModel.search(query: query)
            }
            .onAppear {
                self.favouritesViewModel.startObservingFavourites()
            }
            .onDisappear(perform: {
                self.favouritesViewModel.stopObserving()
            })
        }
    }
}

struct FavouritesList_Previews: PreviewProvider {
    static var previews: some View {
        FavouritesList()
    }
}
