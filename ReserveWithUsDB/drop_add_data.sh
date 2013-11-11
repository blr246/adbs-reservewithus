#!/bin/bash
unalias -a

#set -x

# Get the script directory.
SCRIPT_DIR=`dirname $0`
SCRIPT_NAME=`basename $0`

# Get the data DIR
DATA_DIR="${SCRIPT_DIR}/DB2/Data/"
LOAD_SCRIPT="load.sql"

# Get the schema dir.
SCHEMA_DIR="${SCRIPT_DIR}/DB2/Schema/"
SCHEMA_SCRIPT="hotels.sql"

function usage {
    cat <<EOM
usage: ${SCRIPT_NAME} DATABASE_NAME

Dump and then load ReserveWithUs data to the given database name. The data must
be created already by running gentable-reservewithus.py.

Positional arguments:
 DATABASE_NAME      name of the DB2 database
EOM
}

function dir_exists {
    # Check data, schema directory exists.
    if [[ ! -d "$1" ]]; then
        echo Cannot find $2 directory $1 1>&2
        exit 1
    fi
}

function script_exists {
    # Check data, schema directory exists.
    if [[ ! -e "$1/$2" ]]; then
        echo Cannot find $3 script $2 in $1 1>&2
        exit 1
    fi
}

function verify_state {

    dir_exists "${DATA_DIR}" "data"
    script_exists "${DATA_DIR}" "${LOAD_SCRIPT}" "load"

    dir_exists "${SCHEMA_DIR}" "schema"
    script_exists "${SCHEMA_DIR}" "${SCHEMA_SCRIPT}" "schema"

    # Check data files exist.
    errors=
    for data_file in customer.data hotel.data roomdate.data roomtype.data; do
        if [ ! -e "${DATA_DIR}/${data_file}" ]; then
            errors+=" ${data_file}"
        fi
    done
    if [ -n "${errors}" ]; then
        echo Cannot find data files \{${errors} \} in ${DATA_DIR} 1>&2
        exit 2
    fi

    # Get the DB name.
    database_name="$1"
    shift
    if [ -z "${database_name}" ]; then
        echo Database name not given 1>&2
        usage
        exit 3
    fi
}

# Check command line and verify dependencies.
verify_state $*

db2stop force
db2start
db2 ATTACH TO db2inst1

# Drop and create the database.
db2 DROP DATABASE ${database_name}
db2 CREATE DATABASE ${database_name}
db2 CONNECT TO ${database_name}
pushd ${SCHEMA_DIR} >/dev/null
db2 -tf ${SCHEMA_SCRIPT}
popd >/dev/null

# Load the data.
pushd ${DATA_DIR} >/dev/null
db2 -tf ${LOAD_SCRIPT}
popd >/dev/null

db2 CONNECT TO ${database_name}

exit 0

