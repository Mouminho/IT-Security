#!/bin/sh
# shellcheck disable=SC2006
MYSELF=`which "$0" 2>/dev/null`
# shellcheck disable=SC2181
[ $? -gt 0 -a -f "$0" ] && MYSELF="./$0"
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
# shellcheck disable=SC2154
exec "$java" $java_args -jar  $MYSELF "$@"
exit 1
