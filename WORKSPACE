workspace(name = "slack_integration")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "20079f696c22c733053077b13c0dc6d9902e6b8e",
    #local_path = "/home/<user>/projects/bazlets"
)

load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)

gerrit_api()
