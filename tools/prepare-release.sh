# Prepares a Release
#
# - Removes the SNAPSHOT tag from version
#
#   E.g. increases 2020.1.0-SNAPSHOT to 2020.1.0

# Basic definitions
release_date=$(date --iso-8601)
openems_constants="io.openems.common/src/io/openems/common/OpenemsConstants.java"
package_json="ui/package.json"
package_lock="ui/package-lock.json"
changelog_constants="ui/src/app/changelog/view/component/changelog.constants.ts"

# Reset files
git checkout $openems_constants
git checkout $package_json
git checkout $package_lock
git checkout $changelog_constants

# Find new Version"
major=$(grep 'VERSION_MAJOR =' $openems_constants | sed 's/^.*= \([0-9]\+\);/\1/')
minor=$(grep 'VERSION_MINOR =' $openems_constants | sed 's/^.*= \([0-9]\+\);/\1/')
patch=$(grep 'VERSION_PATCH =' $openems_constants | sed 's/^.*= \([0-9]\+\);/\1/')
new_version="${major}.${minor}.${patch}"
echo "# Release version: $new_version"
echo "#            date: $release_date"

echo "# Update $openems_constants"
sed --in-place 's/\(public .* VERSION_STRING = "\)SNAPSHOT\(".*$\)/\1\2/' $openems_constants

echo "# Update $package_json" 
sed --in-place "s/\(\"version\": \"\).*\(\".*$\)/\1$new_version\2/" $package_json

echo "# Update $package_lock" 
sed --in-place "s/\(^  \"version\": \"\).*\(\".*$\)/\1$new_version\2/" $package_lock

echo "# Update $changelog_constants"
sed --in-place "s/\(UI_VERSION = \).*$/\1\"$version_string\";/" $changelog_constants

echo "# Finished"

echo ""
echo "# Ready for commit: \"Push version to $new_version\""
