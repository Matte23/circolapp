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

struct AttachmentView: View {
    @Environment(\.openURL) var openURL
    var attachmentName: String
    var attachmentUrl: String
    
    var body: some View {
        Divider()
        HStack {
            Text(attachmentName)
                .font(.subheadline)
                .multilineTextAlignment(.leading)
            Spacer()
            
            Button(action: {
                guard let url = URL(string: attachmentUrl) else { return }
                openURL(url)
            }) {
                Image(systemName: "envelope.open")
                    .foregroundColor(.blue)
                    .font(.body)
                    .padding()
            }
            .buttonStyle(PlainButtonStyle())
            
            Button(action: {
                guard let url = URL(string: attachmentUrl) else { return }
                
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
        }
    }
}

struct AttachmentView_Previews: PreviewProvider {
    static var previews: some View {
        AttachmentView(attachmentName: "This is an attachment", attachmentUrl: "http://example.com")
    }
}
