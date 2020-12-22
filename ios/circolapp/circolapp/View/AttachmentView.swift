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
                URLUtils.openUrl(url: attachmentUrl)
            }) {
                Image(systemName: "envelope.open.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20.0, height: 20.0)
                    .padding(8.0)
            }
            .buttonStyle(BorderlessButtonStyle())
            
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
}

struct AttachmentView_Previews: PreviewProvider {
    static var previews: some View {
        AttachmentView(attachmentName: "This is an attachment", attachmentUrl: "http://example.com")
    }
}
