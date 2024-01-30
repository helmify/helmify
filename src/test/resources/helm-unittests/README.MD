## Notes on Helm Unittests

**Helm Unittests are expected in the "tests directory".**

**Helm Unittests are expected to be named "*_test.yaml".**

### Running Helm Unittests w/ Docker on Windows

The docker container for helm unittests assumes you are invoking the command from 
your helm chart's root directory.

On the Windows Commandline use:

```bash
docker run  -v %cd%:/apps helmunittest/helm-unittest .
```

### Assertions
https://github.com/helm-unittest/helm-unittest/blob/main/DOCUMENT.md
