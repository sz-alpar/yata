//
//  ContentView.swift
//  YATA
//
//  Created by Alpár Szotyori on 19.05.24.
//

import SwiftUI
import CoreData

struct ContentView: View {
    @Environment(\.managedObjectContext) private var viewContext

    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \Item.timestamp, ascending: true)],
        animation: .default)
    private var items: FetchedResults<Item>

    @State private var viewModel = ContentViewModel()

    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.todos, id: \.self) { todo in
                    NavigationLink {
                        Text("\(todo.description)")
                    } label: {
                        Text("\(todo.title)")
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
                ToolbarItem {
                    Button(action: addTodo) {
                        Label("Add Item", systemImage: "plus")
                    }
                }
            }
            Text("Select an item")
        }
        .task {
            await viewModel.initialize()
        }
    }

    private func addTodo() {
        // swiftlint:disable:next todo
        // TODO: find out why this task does not update the UI reliably (sometimes UI is updated only on second run)
        Task {
            await viewModel.addTodo(Todo(title: "New todo \(viewModel.todos.count + 1)",
                                         description: "New todo description"))
        }
    }

    private func deleteItems(offsets: IndexSet) {
        withAnimation {
            offsets.map { items[$0] }.forEach(viewContext.delete)

            do {
                try viewContext.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this
                // function in a shipping application, although it may be useful during development.
                let nsError = error as NSError
                fatalError("Unresolved error \(nsError), \(nsError.userInfo)")
            }
        }
    }
}

private let itemFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateStyle = .short
    formatter.timeStyle = .medium
    return formatter
}()

#Preview {
    ContentView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
}
