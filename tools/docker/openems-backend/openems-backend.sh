#!/bin/bash

cd /opt/openems-backend/odoo/addons-enabled
ln -s ../addons-available/partner-contact/partner_firstname
ln -s ../addons-available/web/web_m2x_options
ln -s ../addons-available/odoo-openems/openems
odoo -d prod --addons-path=/opt/openems-backend/odoo/addons-enabled -i base,partner_firstname,web_m2x_options,stock,openems

/usr/bin/java -XX:+ExitOnOutOfMemoryError -Dfelix.cm.dir=/opt/openems-backend/config.d -Djava.util.concurrent.ForkJoinPool.common.parallelism=100 -jar /opt/openems-backend/openems-backend.jar
