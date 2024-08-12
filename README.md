# Uni Luzern Prüfungsabfrage

### Code formatting in pre-commit hook
To have consistent code formatting in the entire project, the formatter-maven-plugin
is used. To ensure proper formatting, there is a pre-commit-hook that can be set up by
running the `setup-pre-commit` shell-script in the root directory. It will copy the
pre-commit script to the `.git/hooks` directory. Please note that this only works if you
don't already have such a script. In this case, either delete the old script or merge the contents
with the new script.