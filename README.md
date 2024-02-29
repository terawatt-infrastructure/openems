# OpenEMS - Terawatt

## Setup

### Secrets
Our OpenEMS deployment requires an AWS Secret `openems` to exist.  This is generated automatically via Terraform.

It must be manually populated with the following:

|Secret Key Name|Info|
|---|---|
|influxdb-password|Generate a secure password|
|influxdb-token|Generate a secure password|
|odoo-password|Generate a secure password|

**NOTE**:  Do this _before_ any further deployments of the application.  These values help bootstrap persistent configuration.

### Odoo
Upon first installation of Odoo (See below for connecting to Odoo), you must active the OpenEMS Addon.

To do this:
- Log in to Odoo
- Go to the `Apps` page
- Locate `OpenEMS`
- Click `Activate`

## UI
When building the UI (as in the Dockerfile), environments are passed in.  As an example, `ng build --base-href "/openems/" -c "openems,openems-backend-prod-eks,prod"`

These environments are defined in the [angular.json](./ui/angular.json) file, which have references to the theme file they represent.  This is the way that the websocket URLs are configured to point to the backend.

## InfluxDB
To connect to influxDB:
- Obtain the password from AWS SSM secret "openems"
- Run:  `kubectl -n openems port-forward svc/openems-influxdb2 8086:8086`
- Login with user `admin` using the password obtained above

## Felix
To connect to felix:
- Run:  `kubectl -n openems port-forward svc/openems-backend-felix 8079:8079`
- Navigate to URL:  http://localhost:8079/system/console/configMgr
- Default credentials are `admin`/`admin`

## Odoo
To connect to Odoo:
- Run:  `kubectl -n openems port-forward svc/openems-odoo 8080:80`
- Navigate to URL:  http://localhost:8080
- Default credentials are `admin`/`admin`

In Odoo, if you want to enable Developer Mode (which allows things like updating the Apps list):
https://www.cybrosys.com/odoo/odoo-books/odoo-16-development/setup-development-environment/developer-mode

## Edge Devices
### Monitoring URL Endpoint
There does not currently seem to be a place to configure the Odoo Edge Monitoring URL from the UI.

To configure this, the current solution is:
- Connect to the Postgres databasse
- Edit the `ir_config_parameter` table data
- Update the `edge_monitoring_url` to the UI's edge URL.  For example, `https://example.com/openems/device/`

## Automated Directory

- Summary: Fork of open-source repository OpenEMS for advanced load management
- Tags:  Advanced Load Management
- Owner(s): ebram@terawattinfrastructure.com, greg@terawattinfrastructure.com
