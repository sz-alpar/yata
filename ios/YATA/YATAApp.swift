//
//  YATAApp.swift
//  YATA
//
//  Created by Alpár Szotyori on 19.05.24.
//

import SwiftUI

@main
struct YATAApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
