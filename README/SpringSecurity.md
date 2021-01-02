# samesite cooki value
## None
## Lax (from subdomain)
## Strict (must exactly match)

## Basic authentication is passing the authorization in HTTP-Header
``` Authorization Basic <BASE64 encoded username:password>```
#### HTTPS hides the creds
## When Spring security is enabled it is going to secure all paths except health and actuator endpoint
#### override default user ``` spring.security.user.name```
#### overriding spring generated UUID as password ``` spring.security.user.password```
## Filter chain <a>https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-architecture</a>