# OpenEMS - Terawatt

## InfluxDB
To connect to influxDB:
- Obtain the password from AWS SSM secret "openems"
- Run:  `kubectl -n openems port-forward svc/openems-influxdb2 8086:8086`
- Login with user `admin` using the password obtained above

## Felix
To connect to felix:
- Run:  `kubectl -n openems  port-forward svc/openems-backend-felix 8079:8079`
- Navigate to URL:  http://localhost:8079/system/console/configMgr
- Default credentials are `admin`/`admin`

## Odoo
To connect to Odoo:
- Run:  `kubectl -n openems  port-forward svc/openems-backend-odoo 8069:8069`
- Navigate to URL:  http://localhost:8069
- Default credentials are `admin`/`admin`

## Automated Directory

- Summary: Fork of open-source repository OpenEMS for advanced load management
- Tags:  Advanced Load Management
- Owner(s): ebram@terawattinfrastructure.com, greg@terawattinfrastructure.com
