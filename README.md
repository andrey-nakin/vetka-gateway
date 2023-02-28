# vetka-gateway

A simple Spring Boot-based GraphQL router.

* Has modularized architecture.
* Has its own GraphQL API for management purposes.

## Modules

* `vetka-gateway-core` - base gateway logic.
* `vetka-gateway-persistence-mongo` - implementation of the persistence layer that uses MongoDB as a storage engine.
* `vetka-gateway-transport-httpclient` - implementation of the transport layer that uses Java `HttpClient`.
* `vetka-gateway-demo` - application demonstrating the packaging of the Vetka Gateway libraries in a single executable
  application.

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
