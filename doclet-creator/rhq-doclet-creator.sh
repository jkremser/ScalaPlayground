#!/bin/bash
_rhq_home=/home/jkremser/workspace/rhq-master

_project="RHQ"
_version="4.9.0"
_description="rhq-server \& core-domain"
wget -O logo.png - https://docs.jboss.org/author/download/attachments/30901932/RHQ > /dev/null 2>&1
_logo="logo.png"
_url="jboss.org/rhq"
_input="$_rhq_home/modules/enterprise/server/jar $_rhq_home/modules/core/domain"

sleep 1.5
./doclet-creator.sh "$_project" "$_version" "$_description" "$_logo" "$_url" "$_input"
rm -f logo.png
