workspace(name = "slack_integration")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "7a9ae377b519934c87184cc05845663ed708b69c",
    #local_path = "/home/<user>/projects/bazlets"
)

load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)

gerrit_api()
