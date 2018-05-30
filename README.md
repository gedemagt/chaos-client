Create new Codename One project
  - Package Name: com.jhalkjar.caoscomp
  - Main Class Name: CaosCompanion
  - Project name: Whatever you like


cd into it

git init
git remote add origin https://github.com/gedemagt/chaos-client.git
git fetch

rm codenameone_*
rm src/com/jhalkjar/caoscomp/CaosCompanion.java
rm src/theme.res

git checkout master

File->Settigs->Java Compiler->1.8

Maybe: Remove <withKotlin> tags form build.xml

Download the following libraries:
  - Codename One JSON Library
  - Codename One Data Access Library

Refresh cn1libs
