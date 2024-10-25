#!/bin/bash

semver_version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout | cut -d '-' -f 1)
short_commit_hash=$(git rev-parse --short HEAD)
release_tag="$semver_version.$short_commit_hash"
echo "Create create release with tag $release_tag (y/n)"
read create_release
if [[ "$create_release" == "y" ]]; then
  git tag "$release_tag"
  git push origin "$release_tag"
fi
