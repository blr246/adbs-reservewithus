#!/bin/bash
unalias -a

function usage {
cat <<EOM
usage: `basename $0` DATABASE_NAME

Kill all active DB2JCC connections for database DATABASE_NAME.
EOM
}

db_name=$1
shift

if [[ -z "${db_name}" ]]; then
    echo Must specify a dababase name.
    usage
    exit 1
fi

# Get list of DB2JCC applications and kill them.
db2 connect to ${db_name}
applications=`db2 list applications | grep 'db2jcc' | cut -f 3 -d ' ' | \
    python -c \
        'import sys; \
         print ", ".join(line.strip() for line in sys.stdin.readlines())'`
db2 force application \( $applications \)
db2 disconnect ${db_name}

