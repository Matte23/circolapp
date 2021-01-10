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

struct NewReminderView: View {
    @State private var reminderDate = Date()
    @Environment(\.presentationMode) var presentationMode
    var circular: Circular
    
    var body: some View {
        NavigationView {
            DatePicker("Pick a date for the reminder", selection: $reminderDate, in: Date()...)
                .datePickerStyle(WheelDatePickerStyle())
                .labelsHidden()
                .padding()
                .navigationBarTitle(Text("New reminder"), displayMode: .inline)
                .navigationBarItems(leading: Button(action: {
                    self.presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Cancel")
                }, trailing: Button(action: {
                    createReminder()
                    self.presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Done")
                })
        }
    }
    
    func createReminder() {
        iOSRepository.getCircularDao().update(id: circular.id, school: circular.school, favourite: circular.favourite, reminder: true, completionHandler: {_,_ in })
        
        let center =  UNUserNotificationCenter.current()
        
        let content = UNMutableNotificationContent()
        content.title = NSString.localizedStringWithFormat(NSLocalizedString("Circular number %@", comment: "") as NSString, String(circular.id)) as String
        content.body = circular.name
        content.sound = UNNotificationSound.default
        content.userInfo["reminder"] = true
        content.userInfo["id"] = circular.id
        content.userInfo["school"] = circular.school
        
        let comps = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: reminderDate)
        let trigger = UNCalendarNotificationTrigger(dateMatching: comps, repeats: false)
        
        let request = UNNotificationRequest(identifier: String(circular.id), content: content, trigger: trigger)
        
        center.add(request) { (error) in
            if error != nil {
                print("Error \(String(describing: error))")
            }
        }
    }
}

struct NewReminderView_Previews: PreviewProvider {
    static var previewCircular = Circular(id: 1, school: 0, name: "This is a circular", url: "http://example.com", date: "19/11/2020", favourite: false, reminder: false, read: false, attachmentsNames: [], attachmentsUrls: [])
    
    static var previews: some View {
        NewReminderView(circular: previewCircular)
    }
}
