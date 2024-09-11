# Uni Luzern Prüfungsabfrage / Exam Feedback Tool (EFT)
## Docker
### Build Image
`docker build . -t uni-luzern`
### Run Image
`docker run --rm  -v ./static:/resources -p 8080:8080 uni-luzern:latest`

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

### Code Quality

The code quality is verified by SonarQube which is executed when a feature branch is merge into the main branch.
The quality gate status can be checked under https://sonar.puzzle.ch/dashboard?id=unilu-exam-feedback-tool .

For local execution you can use the following command:

```
mvn clean verify sonar:sonar -Dsonar.login=<your-sonar-qube-token>
```

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

> Attention: If you are running on Ubuntu 20.04 or later you need to create an AppArmor profile for rootlesskit.
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

To use the installation script, simply run the following command as a non-root user:

`curl -fsSL https://get.docker.com/rootless | sh`

#### Usage
> Attention: Rootless docker can't run systemwide

To now make sure Docker uses the rootless daemon, specify the cli context like this: `docker conext use rootless`.

To control the lifecycle of the daemon use `systemctl --user`.

For example: `systemctl --user start docker`.

*Optional: To run it on system startup run the following:*
```bash
$ systemctl --user enable docker
$ sudo loginctl enable-linger $(whoami)
```