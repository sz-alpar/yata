//
//  ResultExtensions.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

extension Result {

    func mapErrorToRepositoryError() -> Result<Success, DomainError> {
        return mapError { error in
            .repositoryError(error: error)
        }
    }

}
