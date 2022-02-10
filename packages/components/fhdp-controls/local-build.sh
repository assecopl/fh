node prepare-for-build.js
node local-version-up.js
yarn build
yarn pack
cd ../../../fhdp-common/fhdp-commons/fhdp-commons-fh-starter
mvn compile -f pom.xml
cd ../../../npm-packages/components/fhdp-controls/
