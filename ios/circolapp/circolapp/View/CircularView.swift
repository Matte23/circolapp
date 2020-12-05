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

struct CircularView: View {
    @State private var creatingReminder: Bool = false
    @State private var showDetail = false
    var circular: Circular
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text("Circular number " + String(circular.id))
                    .font(.headline)
                Text(circular.date)
                    .font(.subheadline)
                Spacer()
                Button(action: {
                    withAnimation() {
                        self.showDetail.toggle()
                    }
                }) {
                    Image(systemName: "chevron.right.circle")
                        .imageScale(.large)
                        .rotationEffect(.degrees(showDetail ? 90 : 0))
                }
            }
            
            Text(circular.name)
                .font(.body)
            
            if showDetail {
                HStack {
                    Button(action: {
                        guard let url = URL(string: circular.url) else { return }
                        UIApplication.shared.open(url)
                    }) {
                        Image(systemName: "envelope.open")
                            .foregroundColor(.blue)
                            .font(.body)
                            .padding()
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    Button(action: {
                        guard let url = URL(string: circular.url) else { return }
                        
                        let downloadTask = URLSession.shared.downloadTask(with: url) {
                            urlOrNil, responseOrNil, errorOrNil in
                            
                            guard let fileURL = urlOrNil else { return }
                            do {
                                let documentsURL = try
                                    FileManager.default.url(for: .documentDirectory,
                                                            in: .userDomainMask,
                                                            appropriateFor: nil,
                                                            create: false)
                                let savedURL = documentsURL.appendingPathComponent(fileURL.lastPathComponent)
                                try FileManager.default.moveItem(at: fileURL, to: savedURL)
                            } catch {
                                print ("file error: \(error)")
                            }
                        }
                        
                        downloadTask.resume()
                    }) {
                        Image(systemName: "square.and.arrow.down")
                            .foregroundColor(.blue)
                            .font(.body)
                            .padding()
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    Button(action: {
                        iOSRepository.getCircularDao().update(id: circular.id, school: circular.school, favourite: !circular.favourite, reminder: circular.reminder, completionHandler: {_,_ in })
                    }) {
                        Image(systemName: circular.favourite ? "bookmark.fill" : "bookmark")
                            .foregroundColor(.blue)
                            .font(.body)
                            .padding()
                    }
                    .buttonStyle(PlainButtonStyle())
                    
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
                            .foregroundColor(.blue)
                            .font(.body)
                            .padding()
                    }
                    .buttonStyle(PlainButtonStyle())
                    .sheet(isPresented: self.$creatingReminder) {
                        NewReminderView(circular: circular)
                    }
                }
                
                ForEach(0..<circular.attachmentsNames.count, id: \.self) { index in
                    AttachmentView(attachmentName: circular.attachmentsNames[index] as! String, attachmentUrl: circular.attachmentsUrls[index] as! String)
                }
            }
        }
    }
}

struct CircularView_Previews: PreviewProvider {
    static var previewCircular = Circular(id: 1, school: 0, name: "This is a circular", url: "http://example.com", date: "19/11/2020", favourite: false, reminder: false, attachmentsNames: [], attachmentsUrls: [])
    
    static var previews: some View {
        CircularView(circular: previewCircular)
    }
}
