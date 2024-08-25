# Docker Image Build
Builds and publishes a Docker image.

## Parameters
| Parameter | Type           | Description                            | Required |
|-----------|----------------|----------------------------------------|-----------|
| `name`    | `String`       | The name of the Docker image to build. | Yes       |
| `tags`    | `List<String>` | A list of strings to tag Docker image. | Yes       |
| `file`    | `String`       | The Dockerfile location.               | No        |
| `path`    | `String`       | The build context.                     | No        |

## Usage
```groovy
@Library("iv-jenkins@properties") _

DockerImageBuild ([
    "name": "image-name",
    "tags": ["2.0", "latest"],
    "file": "./Dockerfile",
    "path": "."
])

```