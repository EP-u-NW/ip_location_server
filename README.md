# ip_location_server
A GRPC and HTTP server mapping IP addresses to locations. Supports IPv4 and IPv6 and automatic dataset updates.

## Requirements
Java 11. Also, too serve both, IPv4 and IPv6 lookup requests with city level location resolution, about 1.5GB of RAM are needed, since the datasets are stored there for faster access times.

### Building
Build and create a runnable `jar` using maven:
```
mvn package
```
Then you can run it with 
```
java -jar target/app.jar
```

### Docker/Kubernetes
Can be used as a microservice with docker and/or kubernetes, see [Dockerfile](./Dockerfile) and the [kubernetes folder](./kubernetes). Note that using the HTTP `/me` endpoint (and GRPC `Me` call respectively) could return wrong information if your container engine uses some kind of network address translation (NAT). In such a case, if you are running behind a HTTP reverse proxy (e.g. an nginx kubernetes ingress), you could use the `HTTP_ME_USE_HEADER` (and `GRPC_ME_USE_HEADER` respectively) configuration option, which, instead of getting the IP address from the peer in the `/me` request (and GRPC `Me` call respectively), parses it from a header field (such as `X-Forwarded-For`) instead.

## Configuration
Configuration is done using environment variables. Using the default configuration, everything should work out-of-the-box. Here are some general configuration options, while more specific configuration options are explained in the other appropriate sections.

| Environment Variable Name         | Data Type | Default Value                               | Description                                                                                                                                                                                                                                                         |
|-----------------------------------|-----------|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DEBUG                             | boolean   | false                                       | Whether to start in `debug mode` or not. In `debug mode`, additional information are printed to the console.                                                                                                                                                        |

## Dataset
The dataset will be automatically downloaded from GitHub. Also, GitHub is periodically checked for updates to the files containing the datasets. The repository and files to use can be configured, see the configuration section below. If downloading and bulding the internal database from a dataset file fails while updating, the old version will be used, until the next check and succesfull update. The application will not terminate in such a case. On the other hand, if on application start the initial download and database build fails, the application will terminate immediately.

### File Format
A dataset file should either be a utf8 encoded `csv` file, or a `gzip` thereof. The file should have at least three rows, separated by `,`:
```
ip_range_start, ip_range_end, <LOCATION INFORMATION>
```
where `ip_range_start` and `ip_range_end` define the range of ip addresses (inclusive) to which `<LOCATION INFORMATION>` apply.
The IP addresses must be denoted as numbers, e.g. the IPv4 address `1.0.0.0` would be written as `16777216` and`1.0.0.255` would be written as `16777471` whereas the IPv6 address `2001:200::` would be written as `42540528726795050063891204319802818560` and `2001:200:ffff:ffff:ffff:ffff:ffff:ffff` would be written as `42540528806023212578155541913346768895`.
`<LOCATION INFORMATION>` can be an arbitrary string and will be returned as part of the lookup results.
Assume the default file for IPv4 addresses  [`sapics/ip-location-db/geolite2-city/geolite2-city-ipv4-num.csv.gz`](https://github.com/sapics/ip-location-db/blob/master/geolite2-city/geolite2-city-ipv4-num.csv.gz) which has the format
```
ip_range_start, ip_range_end, country_code, state1, state2, city, postcode, latitude, longitude, timezone
```
would be used, then
```
<LOCATION INFORMATION> := country_code, state1, state2, city, postcode, latitude, longitude, timezone
```
Here are some sample lines from that file:
```
47417088,47417343,DE,Baden-Wurttemberg,,Schorndorf,73614,48.8025,9.5317,Europe/Berlin
1303824384,1303824895,DE,Baden-Wurttemberg,,Rudersberg,73635,48.8861,9.5353,Europe/Berlin
1303821824,1303822079,DE,Baden-Wurttemberg,,Stuttgart,70191,48.7670,9.1827,Europe/Berlin
1338376192,1338378239,DE,Baden-Wurttemberg,,Weinstadt-Endersbach,71384,48.8067,9.3738,Europe/Berlin
1338378240,1338378751,DE,Baden-Wurttemberg,,Waiblingen,71332,48.8307,9.3182,Europe/Berlin
1338378752,1338379007,DE,Baden-Wurttemberg,,Korb,74255,49.3519,9.3978,Europe/Berlin
```
It is recommended to check the [`sapics/ip-location-db`](https://github.com/sapics/ip-location-db) GitHub repository since most files there are suitable datasets for this application. It may be a good idea to use other datasets then the default ones (which yield city level location resolution) if you e.g. only need country level resolution, to drastically minimize resource usage (especially RAM and download size).

### Configuration
Note that the `?` in the table is a placeholder, since each of this options can separately be configured for IPv4 (where `?` should be replaced with `4`) and IPv6 (where `?` should replaced with `6`).

| Environment Variable Name         | Data Type | Default Value                               | Description                                                                                                                                                                                                                                                         |
|-----------------------------------|-----------|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| IPV?_ENABLED                      | boolean   | true                                        | Whether IPv? lookups should be enabled or not.                                                                                                                                                                                                                      |
| IPV?_GITHUB_USER                  | string    | sapics                                      | The name of the GitHub user the `IPV?_GITHUB_REPO` is belongs to.                                                                                                                                                                                                   |
| IPV?_GITHUB_REPO                  | string    | ip-location-db                              | The name of the GitHub repository the `IPV?_GITHUB_FILE` is located in.                                                                                                                                                                                             |
| IPV?_GITHUB_FILE                  | string    | geolite2-city/geolite2-city-ipv?-num.csv.gz | The path of the database file in the `IPV?_GITHUB_REPO`.                                                                                                                                                                                                            |
| IPV?_GITHUB_FILE_USE_GZIP         | boolean   | true                                        | Whether the `IPV?_GITHUB_FILE` is compressed using gzip or a plain `csv` file.                                                                                                                                                                                      |
| IPV?_GITHUB_CHECK_INTERVAL_MS     | integer   | 3600000                                     | The delay in milliseconds between checks for newer versions of `IPV?_GITHUB_FILE` in the `IPV?_GITHUB_REPO`. The default value equals one hour.                                                                                                                     |
| IPV?_GITHUB_MAX_FILE_SIZE         | integer   | 104857600                                   | How large in bytes the `IPV?_GITHUB_FILE` can maximally be. If the file is larger, it will not be downloaded to protect the systems RAM usage. A negative number (e.g. `-1`) allows arbitrary large files. The default value equals 100MB.                          |

## HTTP

### Configuration
| Environment Variable Name         | Data Type | Default Value                               | Description                                                                                                                                                                                                                                                         |
|-----------------------------------|-----------|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP_ENABLED                      | boolean   | true                                        | Whether to enable the HTTP server or not.                                                                                                                                                                                                                           |
| HTTP_HOST                         | string    | 0.0.0.0                                     | The address the HTTP server listens on. Default is listening on all IPv4 interfaces.                                                                                                                                                                                |
| HTTP_PORT                         | integer   | 80                                          | The port the HTTP listens on.                                                                                                                                                                                                                                       |
| HTTP_USE_SSL                      | boolean   | false                                       | Whether the HTTP server should be secured using ssl or not. Note that if you set this to `true` you might want to adjust the `HTTP_PORT` to `443` as well.                                                                                                          |
| HTTP_SSL_DIR                      | string    | not set                                     | If this is not set but `GRPC_USE_SSL` is `true` a self signed certificate will be generated and use to secure connections. Otherwise it has to point to a directory where the two files `fullchain.pem` and `privkey.pem` are located.                              |
| HTTP_DETAILED_EXCEPTION_RESPONSES | boolean   | true                                        | If `true` and a Http request results in a error status code (4xx or 5xx) the body of the response will contain a stack trace of what went wrong. If set to `false` the response body will be empty and just the HTTP status code will be transmitted.               |
| HTTP_ME_USE_HEADER                | boolean   | false                                       | If `true` a request to `/me` will not use the peers actual address for the lookup, but the address transmitted in the `HTTP_ME_HEADER_NAME`. Useful when operating behind proxies.                                                                                  |
| HTTP_ME_HEADER_NAME               | string    | X-Forwarded-For                             | The name of the header to parse the ip address from in `/me` requests if `HTTP_ME_USE_HEADER` is `true`. The default value should work with nginx as reverse proxy out-of-the-box.                                                                                  |

### Endpoints
All requests not made to those endpoints will result in a `404 Not Found`.

	GET /me
		Requests:
			Information about the peer that made the request
		Parameters:
			none
		Returns:
			A 200 OK and a utf8 encoded body according to section `Success Response`
			A 400 Bad Request if HTTP_ME_USE_HEADER is enabled, but HTTP_ME_HEADER_NAME did not contain a valid IPv4 or IPv6
			A 403 Forbidden if the target address is IPv? but IPv? lookup is not enabled in the configuration where
			      the target address is either the peers address, or the address from HTTP_ME_HEADER_NAME if HTTP_ME_USE_HEADER is true
			A 405 Method Not Allowed if the endpoint was used with any other HTTP method than GET
			A 500 Internal Server Error if something went wrong internally
	
	GET /lookup
		Requests:
			Information about the IP address in the ip parameter
		Parameters:
			ip: Either a IPv4 or IPv6 address (in IP address notation), may be URL encoded
		Returns:
			A 200 OK and a utf8 encoded body according to section `Success Response`
			A 400 Bad Request if the ip parameter is invalid or is missing
			A 403 Forbidden if the ip parameter is IPv? but IPv? lookup is not enabled in the configuration
			A 405 Method Not Allowed if the endpoint was used with any other HTTP method than GET
			A 500 Internal Server Error if something went wrong internally

### Success Response
On sucess a single line of utf8 encoded text is returned in the body in the format:
```
v?,requested_ip_address,<LOCATION INFORMATION>
```
where
- `v?` is either `v4` or `v6`, depending on what type the requested IP address was 
- `requested_ip_address` is the requested IP address (in IP address notation, not in number notation)
- `<LOCATION INFORMATION>` as defined above
	
A request is also treated as sucess if the request was correct, but no information about the requested IP address could not be found in the dataset. This usually happens for special IP addresses like `127.0.0.1` (which is the localhost loopback address) or addresss from the private network block `192.168.0.0`. In that case `<LOCATION INFORMATION>` is set to the string `null` in the response.

### Examples	
For example, assume the dataset from the previous section, than
```
/lookup?ip=2.211.135.1
```
would (since `2.211.135.1` denoted as number is `47417089`) result in
```
v4,2.211.135.1,DE,Baden-Wurttemberg,,Schorndorf,73614,48.8025,9.5317,Europe/Berlin
```
whereas 
```
/lookup?ip=127.0.0.1
```
would result in
```
v4,127.0.0.1,null
```
since no dataset contains location information for `127.0.0.1`.

## GRPC

### Configuration
| Environment Variable Name         | Data Type | Default Value                               | Description                                                                                                                                                                                                                                                         |
|-----------------------------------|-----------|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GRPC_ENABLED                      | boolean   | true                                        | Whether to enable the GRPC endpoint or not.                                                                                                                                                                                                                         |
| GRPC_HOST                         | string    | 0.0.0.0                                     | The address the GRPC endpoint listens on. Default is listening on all IPv4 interfaces.                                                                                                                                                                              |
| GRPC_PORT                         | integer   | 8081                                        | The port the GRPC endpoint listens on.                                                                                                                                                                                                                              |
| GRPC_USE_SSL                      | boolean   | false                                       | Whether the GRPC endpoint should be secured using ssl or not.                                                                                                                                                                                                       |
| GRPC_SSL_DIR                      | string    | not set                                     | If this is not set but `GRPC_USE_SSL` is `true` a self signed certificate will be generated and use to secure connections. Otherwise it has to point to a directory where the two files `fullchain.pem` and `privkey.pem` are located.                              |
| GRPC_ME_USE_HEADER                | boolean   | false                                       | If `true` a request to the `Me` endpoint will not use the peers actual address for the lookup, but the address transmitted in the `GRPC_ME_HEADER_NAME`. Useful when operating behind proxies.                                                                      |
| GRPC_ME_HEADER_NAME               | string    | X-Forwarded-For                             | The name of the header to parse the ip address from in `Me` requests if `GRPC_ME_USE_HEADER` is `true`. The default value should work with nginx as reverse proxy out-of-the-box.                                                                                   |

### Service
The GRPC service endpoints are similar to the HTTP endpoints. For a detailed explanation, please see [ip_location_server.proto](./grpc/ip_location_server.proto).