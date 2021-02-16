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
    @State private var sharingPhone: Bool = false
    @State private var sharingPad: Bool = false
    @State private var loadingRealUrl = false
    
    var index: Int
    var circular: Circular
    
    let attachmentName: String
    let attachmentUrl: String
    var realAttachmentUrl: String = ""
    
    init(index: Int, circular: Circular) {
        self.index = index
        self.circular = circular
        
        attachmentName = circular.attachmentsNames[index] as! String
        attachmentUrl = circular.attachmentsUrls[index] as! String
        
        if circular.realAttachmentsUrls.count == circular.attachmentsUrls.count {
            realAttachmentUrl = circular.realAttachmentsUrls[index] as! String
        }
    }

    var body: some View {
        Divider()
        HStack {
            Text(attachmentName)
                .font(.subheadline)
                .multilineTextAlignment(.leading)
            Spacer()
            
            if loadingRealUrl {
                ActivityIndicator(isAnimating: .constant(true), style: .medium)
                    .padding(8.0)
            } else {
                Button(action: {
                    runWhenUrlIsAvailable(code: {realUrl in URLUtils.openUrl(url: realUrl)})
                }) {
                    Image(systemName: "envelope.open.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20.0, height: 20.0)
                        .padding(8.0)
                }
                .buttonStyle(BorderlessButtonStyle())
            }
            
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
                    .padding(8.0)
            }
            .buttonStyle(BorderlessButtonStyle())
            .sheet(isPresented: $sharingPhone) {
                URLUtils.shareSheetView(url: attachmentUrl)
            }
            .popover(isPresented: $sharingPad) {
                URLUtils.shareSheetView(url: attachmentUrl)
            }
        }
    }
    
    func runWhenUrlIsAvailable(code: @escaping (_ url: String) -> Void) {
        if realAttachmentUrl == "" {
            loadingRealUrl = true
            
            iOSRepository.getCircularRepository().getRealUrlForAttachment(index: Int32(index), rawUrls: circular.attachmentsUrls as! [String], realUrls: circular.realAttachmentsUrls as! [String], id: circular.id, school: circular.school, completionHandler: {realUrls, _ in
                
                if realUrls == nil {
                    code(attachmentUrl)
                }
                
                loadingRealUrl = false
                code(realUrls![index])
            } )
            return
        }

        code(realAttachmentUrl)
    }
}

struct AttachmentView_Previews: PreviewProvider {
    static var previewCircular = Circular(id: 1, school: 0, name: "This is a circular", url: "http://example.com", realUrl: "http://example.com", date: "19/11/2020", favourite: false, reminder: false, read: false, attachmentsNames: ["This is an attachment"], attachmentsUrls: ["http://example.com"], realAttachmentsUrls: [])
    
    static var previews: some View {
        AttachmentView(index: 0, circular: previewCircular)
    }
}
