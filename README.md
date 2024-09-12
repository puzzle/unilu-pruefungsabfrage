# Uni Luzern Prüfungsabfrage / Exam Feedback Tool (EFT)
## Docker
### Build Image
`docker build . -t uni-luzern`
### Run Image
`docker run --rm  -v ./static:/resources -p 8080:8080 uni-luzern:latest`

## Development
### Hot reload
To use hot reload start the project with `docker compose up -d`.
This will start the application. The application will be available at `http://localhost:8080`
To debug the application you can just use the provided Intelij run config `ExamFeedbackTool Debug`.
If you use Intelij you have to make the following adjustments:
- [Enable auto build](https://stackoverflow.com/a/12744431)

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
