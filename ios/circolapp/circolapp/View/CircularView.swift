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
import Shared

struct CircularView: View {
    @State private var creatingReminder: Bool = false
    @State private var sharingPhone: Bool = false
    @State private var sharingPad: Bool = false
    @State private var showDetail = false
    @State private var loadingRealUrl = false
    
    var circular: Circular
    var idIsHumanReadable = true
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                if idIsHumanReadable {
                    Text("Circular number \(String(circular.id))")
                        .font(.headline)
                        .fontWeight(circular.read ? .regular : .bold)
                    Text(circular.date)
                        .font(.subheadline)
                        .fontWeight(circular.read ? .regular : .bold)
                } else {
                    Text("Circular published on \(String(circular.date))")
                        .font(.headline)
                        .fontWeight(circular.read ? .regular : .bold)
                }
                Spacer()
                Button(action: {
                    self.showDetail.toggle()
                }) {
                    Image(systemName: "chevron.right.circle")
                        .foregroundColor(Color("AccentColor"))
                        .imageScale(.large)
                        .rotationEffect(.degrees(showDetail ? 90 : 0))
                }
            }
            
            Text(circular.name)
                .font(.body)
            
            if showDetail {
                HStack {
                    if loadingRealUrl {
                        ActivityIndicator(isAnimating: .constant(true), style: .medium)
                            .padding(.leading, 32.0)
                    } else {
                        Button(action: {
                            if !circular.read {
                                iOSRepository.getCircularDao().markRead(id: circular.id, school: circular.school, read: true, completionHandler: {_,_ in })
                            }
                            
                            runWhenUrlIsAvailable(code: {realUrl in URLUtils.openUrl(url: realUrl)})
                        }) {
                            Image(systemName: "envelope.open.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20.0, height: 20.0)
                                .padding(.leading, 32.0)
                        }
                        .buttonStyle(BorderlessButtonStyle())
                    }
                    
                    Spacer()
                    
                    Button(action: {
                        if UIDevice.current.userInterfaceIdiom == .phone {
                            sharingPhone = true
                        } else {
                            sharingPad = true
                        }
                    }) {
                        Image(systemName: "square.and.arrow.up.fill")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 20.0, height: 20.0)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                    .sheet(isPresented: $sharingPhone) {
                        URLUtils.shareSheetView(url: circular.url)
                    }
                    .popover(isPresented: $sharingPad) {
                        URLUtils.shareSheetView(url: circular.url)
                    }
                    
                    Spacer()
                    
                    Button(action: {
                        iOSRepository.getCircularDao().update(id: circular.id, school: circular.school, favourite: !circular.favourite, reminder: circular.reminder, completionHandler: {_,_ in })
                    }) {
                        Image(systemName: circular.favourite ? "bookmark.fill" : "bookmark")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 20.0, height: 20.0)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                    
                    Spacer()
                    
                    Button(action: {
                        if circular.reminder {
                            let center =  UNUserNotificationCenter.current()
                            center.removePendingNotificationRequests(withIdentifiers: [String(circular.id)])
                            
                            iOSRepository.getCircularDao().update(id: circular.id, school: circular.school, favourite: circular.favourite, reminder: false, completionHandler: {_,_ in })
                            
                            return
                        }
                        
                        self.creatingReminder = true
                    }) {
                        Image(systemName: circular.reminder ? "alarm.fill" : "alarm")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 20.0, height: 20.0)
                            .padding(.trailing, 32.0)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                    .sheet(isPresented: self.$creatingReminder) {
                        NewReminderView(circular: circular)
                    }
                }
                .padding(.vertical, 8.0)
                
                
                ForEach(0..<circular.attachmentsNames.count, id: \.self) { index in
                    AttachmentView(index: index, circular: circular)
                }
            }
        }
    }
    
    func runWhenUrlIsAvailable(code: @escaping (_ url: String) -> Void) {
        if circular.realUrl == nil {
            loadingRealUrl = true
            iOSRepository.getCircularRepository().getRealUrl(rawUrl: circular.url, id: circular.id, school: circular.school, completionHandler:
                                                                {realUrl, _ in
                                                                    loadingRealUrl = false
                                                                    code(realUrl!)
                                                                })
            return
        }

        code(circular.realUrl!)
    }
}

struct CircularView_Previews: PreviewProvider {
    static var previewCircular = Circular(id: 1, school: 0, name: "This is a circular", url: "http://example.com", realUrl: "http://example.com", date: "19/11/2020", favourite: false, reminder: false, read: false, attachmentsNames: [], attachmentsUrls: [], realAttachmentsUrls: [])
    
    static var previews: some View {
        CircularView(circular: previewCircular)
    }
}
