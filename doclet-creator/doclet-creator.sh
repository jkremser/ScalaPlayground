#!/bin/bash
# author jkremser@redhat.com

declare -r TRUE=0
declare -r FALSE=1
function isYes() {
  local x=$1
  [ "x$x" = "xy" ] || [ "x$x" = "xY" ] || [ "x$x" = "xyes" ]  && echo $TRUE; return
  echo $FALSE
}

# check if doxygen exists
$(type doxygen &> /dev/null) || { 
  echo "Doxygen is not installed, please install it first." 
  exit 1
}

if [ $# -ne 6 ]; then
echo ""
  read -p "Project Name (default=Foo): " _project
  read -p "Version (default=1.0): " _version
  read -p "Description (default=Description): " _description
  read -p "Path to logo file (max-size: w=200px h=55px, default=logo.png): " _logo
  read -p "Project URL (default=www.foo.com): " _url
  read -p "Space separated paths of source directories (default=.): " _input
else 
  echo "all set"
  _project="$1"
  _version="$2"
  _description="$3"
  _logo="$4"
  _url="$5"
  _input="$6"
fi

# defaults
_project=${_project:-Foo}
_version=${_version:-1.0}
_description=${_description:-Description}
_logo=${_logo:-logo.png}
_url=${_url:-www.foo.com}
_input=${_input:-.}

# sanitize slashes
_project_sanitized=$(echo $_project| sed 's/\//\\\//g')
_version_sanitized=$(echo $_version| sed 's/\//\\\//g')
_description_sanitized=$(echo $_description| sed 's/\//\\\//g')
_logo_sanitized=$(echo $_logo| sed 's/\//\\\//g')
_url_sanitized=$(echo $_url| sed 's/\//\\\//g')
_input_sanitized=$(echo $_input| sed 's/\//\\\//g')

echo ""
echo "-----------------------------------------------"
echo "Project Name: $_project"
echo "Version:  $_version"
echo "Description: $_description"
echo "Logo: $_logo"
echo "URL: $_url"
echo "Source input: $_input"
echo "-----------------------------------------------"
read -p "Create the Doclet? (Y/n): " createDocletTest
createDocletTest=${createDocletTest:-y}

function createDoclet {
  cp template "$_project"
  sed -i.bak -e "s/@project/$_project_sanitized/g" -e "s/@version/$_version_sanitized/g" -e "s/@description/$_description_sanitized/g" -e "s/@logo/$_logo_sanitized/g" -e "s/@url/$_url_sanitized/g" -e "s/@input/$_input_sanitized/g" "$_project"

  echo "<br/><br/>" > footer.html
  # todo: ask if we should include the source code
  doxygen "$_project"

  (cd html && cat Info.plist | sed -e "s/<\/dict>/     <key>isDashDocset<\/key>\n     <true\/>\n<\/dict>/" > Info.plist2 && mv Info.plist2 Info.plist)
  (cd html && cat Makefile | grep -v "XCODE_INSTALL" | sed -e "s/DESTDIR=.*/DESTDIR=../" -e "0,/DOCUMENTS)/s//DOCUMENTS) \&\& rm -f ..\/docSet.dsidx \&\& (cd .. \&\& sbt \"run .\/html\/Tokens.xml\" \&\& cd -) \&\& cp ..\/docSet.dsidx \$(DOCSET_RESOURCES)/" > Makefile2 && mv Makefile2 Makefile && make install)

  tar --exclude='.DS_Store' -cvzf "$_project"".tgz" "$_project"".docset"

  # cleanup
  rm -rf "footer.html" "html" "$_project" "$_project"".bak" "Makefile_bak" "docSet.dsidx"
}

if [ "$(isYes $createDocletTest)" = "$TRUE" ]; then
  echo "Creating..."
  createDoclet
  echo "Done."
  echo ""
fi

