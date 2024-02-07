# OpenEMS - Terawatt

## InfluxDB
To connect to influxDB:
- Obtain the password from AWS SSM secret "openems"
- Run:  `kubectl -n openems port-forward svc/openems-influxdb2 8086:8086`
- Login with user `admin` using the password obtained above

## Automated Directory

- Summary: Fork of open-source repository OpenEMS for advanced load management
- Tags:  Advanced Load Management
- Owner(s): ebram@terawattinfrastructure.com, greg@terawattinfrastructure.com
