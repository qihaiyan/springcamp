CREATE TABLE "ROUTE_FILTER_ENTITY"
(
id VARCHAR(255) PRIMARY KEY,
route_id VARCHAR(255),
code VARCHAR(255),
url VARCHAR(255)
);
insert into ROUTE_FILTER_ENTITY values('1','routeOne','alpha','http://httpbin.org/anything?a=test');