#!/bin/bash

#local _project, _version, _description, _logo, _url, _footer
_project="RHQ"
_version="4.9.0"
_description="rhq-server \& core-domain"
_logo="\/home\/jkremser\/Downloads\/RHQ.png"
_url="jboss.org\/rhq"
_footer="<a href='jboss.org\/rhq'>Project page<\/a>"


sed -i.bak -e "s/@project/$_project/g" -e "s/@version/$_version/g" -e "s/@description/$_description/g" -e "s/@logo/$_logo/g" -e "s/@url/$_url/g" -e "s/@footer/$_footer/g" $1


#-e s/@description/$_description/g -e s/@logo/$_logo/g -e s/@url/$_url/g -e s/@footer/$_footer/g $1

#todo: set SOURCE_BROWSER to NO
#consider HIDE_UNDOC_CLASSES>
