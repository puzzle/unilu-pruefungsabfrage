# Setup guide
## Prerequisites
- [Docker](https://nodejs.org/en/)

## Docker rootless
There are two installation approaches.

### Method 1 - Via package manager (recommended if possible):
With Docker 20.10 and later you should already have the rootless installation script at
`/usr/bin/dockerd-rootless-setuptool.sh`. If not you should be able to get it by installing the following
package: `sudo apt-get install -y docker-ce-rootless-extras`

If you have the script you can install rootless docker by running the following command as non-root user:
`dockerd-rootless-setuptool.sh install`

### Method 2 - Via installation script
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

## Start docker container
To start the docker container, run the following command:
```bash
`docker run --rm  -v ./static:/resources -p 8080:8080 uni-luzern:latest`
```