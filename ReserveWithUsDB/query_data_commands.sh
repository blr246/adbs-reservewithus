#!/bin/bash

function make_query {
    outfile=$1
    shift
    query="$*"
    export_prefix="EXPORT TO ${outfile}.csv OF DEL MODIFIED BY NOCHARDEL"
    echo "${export_prefix} ${query}"
}

db2 attach db2inst1
db2 connect to tuning

db2 `make_query \
    'country_city_counts' \
    'select count(*), country, city from hotel group by country, city'`

db2 `make_query \
    'date_counts' \
    'select count(*), single_day_date from room_date group by single_day_date'`
