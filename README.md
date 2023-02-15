# vetka-gateway

Simple GraphQL router written in Java and WebFlux framework.

* Has its own GraphQL API for management purposes.
* Stores configuration in MongoDB.

## Configuration

By default Vetka Gateway listens to management requests on `/graphql` address.

### Add GraphQL endpoint (underlying service)

```graphql
mutation {
    createGraphQlEndpoint(
        input: {
            name: "my-api",
            address: "https://my-api.io/graphql",
            schema: "type Country {\n  code: ID!\n  name: String!\n}\n\ninput StringQueryOperatorInput {\n  eq: String\n  ne: String\n  in: [String]\n  nin: [String]\n  regex: String\n  glob: String\n}\n\ninput CountryFilterInput {\n  code: StringQueryOperatorInput\n  currency: StringQueryOperatorInput\n  continent: StringQueryOperatorInput\n}\n\ntype Query {\n  countries(filter: CountryFilterInput): [Country!]!\n}\n"
            httpVersion: "1.1"
            connectTimeout: 30
            readTimeout: 500
        }
    ) {
        ...on GraphQlEndpointCreationResponse {
            graphQlEndpoint {
                id
                name
                address
                schema
            }
        }
        ...on GraphQlEndpointCreationErrors {
            errors {
                ...on EndpointErrorEmptyName {
                    __typename
                    message
                }
                ...on EndpointErrorDuplicatingName {
                    __typename
                    message
                    name
                }
                ...on GraphQlEndpointErrorBadSchema {
                    __typename
                    message
                    errors {
                        message
                        locations {
                            line
                            column
                        }
                    }
                }
                ...on IError {
                    __typename
                    message
                }
            }
        }
    }
}
```

### Edit GraphQL endpoint

```graphql
mutation {
    updateGraphQlEndpoint(
        input: {
            id: "63eb7eea6cd73c172052175e"
            name: "my-api",
            address: "https://my-api.io/graphql",
            schema: "type Country {\n  code: ID!\n  name: String!\n}\n\ninput StringQueryOperatorInput {\n  eq: String\n  ne: String\n  in: [String]\n  nin: [String]\n  regex: String\n  glob: String\n}\n\ninput CountryFilterInput {\n  code: StringQueryOperatorInput\n  currency: StringQueryOperatorInput\n  continent: StringQueryOperatorInput\n}\n\ntype Query {\n  countries(filter: CountryFilterInput): [Country!]!\n}\n"
            httpVersion: "2"
            connectTimeout: 20
            readTimeout: 300
        }
    ) {
        ...on GraphQlEndpointCreationResponse {
            graphQlEndpoint {
                id
                name
                address
                schema
            }
        }
        ...on GraphQlEndpointCreationErrors {
            errors {
                ...on EndpointErrorUnknownId {
                    __typename
                    message
                    id
                }
                ...on EndpointErrorEmptyName {
                    __typename
                    message
                }
                ...on EndpointErrorDuplicatingName {
                    __typename
                    message
                    name
                }
                ...on GraphQlEndpointErrorBadSchema {
                    __typename
                    message
                    errors {
                        message
                        locations {
                            line
                            column
                        }
                    }
                }
                ...on IError {
                    __typename
                    message
                }
            }
        }
    }
}
```

### Delete GraphQL endpoint

```graphql
mutation {
    deleteEndpoint(
        id: "63eb7eea6cd73c172052175e"
    ) {
        ...on EndpointDeletionResponse {
            id
        }
        ...on EndpointDeletionErrors {
            errors {
                ...on EndpointErrorUnknownId {
                    __typename
                    message
                    id
                }
                ...on IError {
                    __typename
                    message
                }
            }
        }
    }
}
```
