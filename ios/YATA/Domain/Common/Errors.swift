//
//  Errors.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

enum DomainError: Error {
    case repositoryError(error: Error)
}
