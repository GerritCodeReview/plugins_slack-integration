workspace(name = "slack_integration")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "a029d8e41d6211c8b23052aa0a0c2c7649577e85",
    #local_path = "/home/<user>/projects/bazlets"
)

load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)

gerrit_api()
