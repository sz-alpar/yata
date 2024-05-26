//
//  UserRepository.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

protocol UserRepository {

    func findActiveUser() async -> Result<User?, Error>

    func getAllUsers() async -> Result<[User], Error>

    func updateUsers(_ users: [User]) async -> Result<Void, Error>

    func addUser(_ user: User) async -> Result<Void, Error>

    func updateUser(_ user: User) async -> Result<Void, Error>

}
