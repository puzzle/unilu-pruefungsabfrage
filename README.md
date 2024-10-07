# Uni Luzern Prüfungsabfrage / Exam Feedback Tool (EFT)
## Docker
### Build Image
`docker build . -t unilu-pruefungsabfrage`

### Run Image
`docker run --rm  -v ./static:/resources -p 8080:8080 unilu-pruefungsabfrage:latest`

## Development
### Hot reload
To use hot reload start the project with `docker compose up -d`.
This will start the application. The application will be available at `http://localhost:8080`
To debug the application you can just use the provided Intelij run config `ExamFeedbackTool Debug`.

### Code Formatting in pre-commit hook
To have consistent code formatting in the entire project, the formatter-maven-plugin
is used. To ensure proper formatting, there is a pre-commit-hook that can be set up by
running the `setup-pre-commit` shell-script in the root directory. It will copy the
pre-commit script to the `.git/hooks` directory. Please note that this only works if you
don't already have such a script. In this case, either delete the old script or merge the contents
with the new script.

To change formatting preferences, you can export your formatting settings as eclipse
code formatter profile file, call it formatting(.xml) and replace the old config file in the
resources directory.

## Security headers
To test the security headers of the application, you can use the following command:
`docker compose --profile zap up -d `

This will create the report (`zap-report.html`) in `/zap` which you can open in your browser to see the
scan results. The scan results contain all found alerts and a more detailed insight on every single one with a description
of the alert, the request that triggered it, solution, reference etc.

### Code Quality

The code quality is verified by SonarQube which is executed when a feature branch is merge into the main branch.
The quality gate status can be checked under https://sonar.puzzle.ch/dashboard?id=unilu-exam-feedback-tool .

For local execution you can use the following command:

```
mvn clean verify sonar:sonar -Dsonar.login=<your-sonar-qube-token>
```

### The release workflow
To release a new version of the tool we have a release pipeline. This pipeline is triggered when a tag with the format <br>
`<semver-version>.<short-commit-hash>` is pushed. The short commit hash must have seven characters for the pipeline to start.

> **Attention:** The project version on Dependency Track is set as `<major-version>.<minor-version>.x`, so the first two numbers of
the semver version. Since Dependency Track tracks every version of a project independently we should only increase the major or minor versions
for code states want to track independently.

The pipeline executes the following steps:

1. Check out the tagged commit
2. Assert that the semver version in the tag matches the semver version in the pom.xml.
3. Assert that the short commit hash in the tag matches the one from the checked out commit.
4. Build and test the jar file
5. If the tests succeed, a release on Github is created
6. The docker image is built and pushed to the Github Container Registry (GHCR).
7. If this all succeeds a new job creates the sbom and pushes it to Dependency Track.

To create a clean release you should follow the following steps:
1. Create a new branch on which you update the semver version in the pom.xml.
2. Create a pull request with the new version and merge if ready.
3. Check out the main branch and pull the latest changes.
4. When you are ready to release execute the `create-release.sh` script in the root directory of the project.
This will create the correct release tag to create a release from your locally checked out commit. After confirming the
creation of the tag it will be pushed to Github and the release-workflow starts.


### Development with rootless docker setup
> Attention: The steps below are for Ubuntu or an Ubuntu based distribution like Pop!_OS. For more
> detailed steps and/or instructions for other distributions please refer to the official docker documentation:
> https://docs.docker.com/engine/security/rootless/

#### Prerequisites
Install the following packages
  - dbus-user-session - `sudo apt-get install -y dbus-user-session` (relogin after installation)
  - uidmap - `sudo apt-get install -y uidmap`

Before installing rootless docker you should disable the systemwide Docker installation by running the following commands:
```bash
$ sudo systemctl disable --now docker.service docker.socket
$ sudo rm /var/run/docker.sock
```

#### Installation
There are two installation approaches.

##### Method 1 - Via package manager:
With Docker 20.10 and later you should already have the rootless installation script at
`/usr/bin/dockerd-rootless-setuptool.sh`. If not you should be able to get it by installing the following
package: `sudo apt-get install -y docker-ce-rootless-extras`

If you have the script you can install rootless docker by running the following command as non-root user:
`dockerd-rootless-setuptool.sh install`

##### Method 2 - Via installation script
This approach is helpful if you dont have permission to download packages or similar.

> Attention: If you are running on Ubuntu 24.04 or later you need to create an AppArmor profile for rootlesskit.
> This profile is already included in the deb package but needs to created manually with this method. For more information
> on why this needs to be done, please refer to the official documentation here:
> https://docs.docker.com/engine/security/rootless/#distribution-specific-hint.
> 
> To create the profile run the following commands:
> ```bash
> $ filename=$(echo $HOME/bin/rootlesskit | sed -e s@^/@@ -e s@/@.@g)
> 
> $ cat <<EOF > ~/${filename}
> abi <abi/4.0>,
> include <tunables/global>
> 
> "$HOME/bin/rootlesskit" flags=(unconfined) {
> userns,
> 
> include if exists <local/${filename}>
> }
> EOF
> 
> $ sudo mv ~/${filename} /etc/apparmor.d/${filename}
> ```
> After that you have to restart AppArmor: `systemctl restart apparmor.service`

To use the installation script, simply run the following command as a non-root user:

`curl -fsSL https://get.docker.com/rootless | sh`

#### Usage
> Attention: Rootless docker can't run systemwide

To now make sure Docker uses the rootless daemon, specify the cli context like this: `docker context use rootless`.

To control the lifecycle of the daemon use `systemctl --user`.

For example: `systemctl --user start docker`.

*Optional: To run it on system startup run the following:*
```bash
$ systemctl --user enable docker
$ sudo loginctl enable-linger $(whoami)
```

You can now use the normal Docker commands to build and start the image like described at the top of this readme.