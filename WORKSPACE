workspace(name = "slack_integration")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "53cfe52b1b691040fd03dfe025c0a381df48c6ee",
    #local_path = "/home/<user>/projects/bazlets"
)

load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)

gerrit_api()
