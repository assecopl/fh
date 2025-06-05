
### Konfiguracja FH bez bazy danych JDBC 


Aby uruchomić FH bez bazy danych należy aktywować profil springowy ``withoutDataSource``, np. jako parametr uruchomieniowy JVM
```
-Dspring.profiles.active=prod,withoutDataSource
```
lub jako wpis w ``application.properties``
```
spring.profiles.active=prod,withoutDataSource 
```
Oczywiście nie można używać modułów FH lub innych bibliotek korzystających z DataSource, np.: ``securityDataProvider-jdbc``, ``permissionProvider-jdbc``

Nie można korzystać ze Springowych DataSource JPA, np.: ``EndpointRepository``

Można usunąć (lub excludować) zależności do ``fhJpa-standalone``, ``fhJpa-jee``

W przypadku bezpieczeństwa, moduły ``securityDataProvider-jdbc``, ``permissionProvider-jdbc`` można zastąpić odpowiednikami REST (``securityDataProvider-rest``, ``permissionProvider-rest``)

Przykładowa konfiguracja:
```
-Dfhframework.security.provider.rest.templateType=Simple
-Dfhframework.security.permission.rest.rest-uri=http://localhost:8070/perm
-Dfhframework.security.provider.rest.idpUri=http://localhost:8070/idp
-Dspring.profiles.active=dev,withoutDataSource 
```